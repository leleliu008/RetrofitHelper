package com.fpliu.newton.http.download

import com.fpliu.newton.http.OKHttpRequest
import com.fpliu.newton.http.RetrofitRequest
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import okio.BufferedSink
import okio.Okio
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.IOException

/**
 *
 * @author 792793182@qq.com 2018-04-25.
 */
object Downloader {

    private val downloadAPI: DownloadAPI = RetrofitRequest.getRetrofit().create(DownloadAPI::class.java)

    var downloadInfoPersistent: DownloadInfoPersistent = DownloadInfoPersistent2File()

    /**
     * 异步下载资源，并保存到指定文件
     *
     * @param url     下载地址
     * @param desFile 保存到的路径
     */
    fun downloadAsync(url: String, desFile: File): Observable<DownloadInfo> {
        return Observable
                .just("")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { downloadSync(url, desFile) }
    }

    /**
     * 同步下载资源，并保存到指定文件
     *
     * @param url     下载地址
     * @param desFile 保存到的路径
     */
    @Throws(IOException::class)
    fun downloadSync(url: String, desFile: File): DownloadInfo {
        val downloadInfo = downloadInfoPersistent.read(desFile)
        return handleResponse(request(url, desFile, downloadInfo), desFile, downloadInfo, 0)
    }

    @Throws(IOException::class)
    private fun request(url: String, retryCount: Int): Response<ResponseBody> {
        return try {
            downloadAPI.download(url).execute()
        } catch (e: IOException) {
            if (retryCount < 5) {
                request(url, retryCount + 1)
            } else {
                throw e
            }
        }
    }

    @Throws(IOException::class)
    private fun request(url: String, eTag: String, retryCount: Int): Response<ResponseBody> {
        return try {
            downloadAPI.download(url, eTag).execute()
        } catch (e: IOException) {
            if (retryCount < 5) {
                request(url, eTag, retryCount + 1)
            } else {
                throw e
            }
        }
    }

    @Throws(IOException::class)
    private fun continueRequest(url: String, currentLength: Long, totalByte: Long, retryCount: Int): Response<ResponseBody> {
        return try {
            downloadAPI.continueDownload(url, "bytes=$currentLength-$totalByte").execute()
        } catch (e: IOException) {
            if (retryCount < 5) {
                continueRequest(url, currentLength, totalByte, retryCount + 1)
            } else {
                throw e
            }
        }
    }

    /**
     * 发起下载请求
     *
     * @param url     下载地址
     * @param desFile 保存到的路径
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun request(url: String, desFile: File, downloadInfo: DownloadInfo?): retrofit2.Response<ResponseBody> {
        //如果文件存在
        if (desFile.exists()) {
            if (downloadInfo != null) {
                val eTag = downloadInfo.eTag
                val totalByte = downloadInfo.totalByte
                val currentLength = desFile.length()

                when {
                    totalByte == currentLength -> { //空文件，去下载整个文件
                        return if (totalByte == 0L) {
                            request(url, 0)
                        } else {
                            //本地没有缓存下ETag，你不知道这个文件在服务器上到底有没有更新，所以，即使你本地有这个完整的文件，也要去下载
                            if (eTag == null || eTag == "") {
                                desFile.delete()
                                request(url, 0)
                            } else {
                                request(url, eTag, 0)
                            }
                        }
                    }
                    totalByte < currentLength -> { //说明文件是完整的
                        desFile.delete()
                        return request(url, 0)
                    }
                    else -> return continueRequest(url, currentLength, totalByte, 0)
                }// totalByte > currentLength 应该是没有下载完，断点续传吧
                //你的本地文件比上一次获得的文件大小还大，这个文件很可能是遭到了人为的篡改，删掉他，重新完整的下载
            } else {
                return request(url, 0)
            }//应该是用户把缓存清除了，导致缓存的元数据丢失，那就只好重新下载了
        } else {
            return request(url, 0)
        }//本地没有缓存，说明是第一次下载
    }

    /**
     * 处理响应
     *
     * @param response
     * @param desFile
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun handleResponse(response: retrofit2.Response<ResponseBody>, desFile: File, downloadInfo: DownloadInfo?, retryCount: Int): DownloadInfo {
        val httpCode = response.code()

        //重定向了，重新发起请求
        if (httpCode == 303) {
            //这里得限制重定向的次数，避免递归层次过高导致崩溃
            if (retryCount < 5) {
                val newURL = response.headers().get("Location") ?: ""
                return handleResponse(request(newURL, desFile, downloadInfo), desFile, downloadInfo, retryCount + 1)
            } else {
                throw RuntimeException("重定向的次数过多")
            }
        } else if (httpCode == 304) {
            return downloadInfo ?: downloadInfoPersistent.read(desFile) ?: DownloadInfo()
        } else if (httpCode == 206) {
            OKHttpRequest.saveFile(response.raw(), response.body()!!, desFile, desFile.length(), null)
            return downloadInfo ?: downloadInfoPersistent.read(desFile) ?: DownloadInfo()
        } else if (httpCode == 200) {
            val responseHeaders = response.headers()
            val eTag = responseHeaders.get("ETag")
            val lastModified = responseHeaders.get("Last-Modified")
            val contentLengthStr = responseHeaders.get("Content-Length")
            var contentLength: Long
            if (contentLengthStr == null || contentLengthStr == "") {
                contentLength = 0
            } else {
                try {
                    contentLength = contentLengthStr.toLong()
                } catch (e: Exception) {
                    contentLength = 0
                    e.printStackTrace()
                }
            }

            //保存这个文件下载相关的元数据
            val newDownloadInfo = DownloadInfo(desFile.absolutePath, lastModified, contentLength, eTag, true)

            //确保要保存到的这个路径中的文件夹都事先创建好
            desFile.parentFile?.run {
                if (!exists()) {
                    mkdirs()
                }
            }

            downloadInfoPersistent.write(desFile, newDownloadInfo)

            //写文件，无论是否发生异常，一定要关闭文件流，如果发生了异常，直接抛给上层，上层处理即可
            var bufferedSink: BufferedSink? = null
            try {
                bufferedSink = Okio.buffer(Okio.sink(desFile))
                bufferedSink!!.writeAll(response.body()!!.source())
            } finally {
                if (bufferedSink != null) {
                    bufferedSink.close()
                }
            }
            return newDownloadInfo
        }//正常从头开始下载
        //服务器支持断点续传
        //服务器上的资源没有变化，可以使用本地缓存
        //出现了异常，抛出异常，通知上层即可
        throw HttpException(response)
    }
}