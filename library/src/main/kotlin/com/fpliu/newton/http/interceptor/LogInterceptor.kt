package com.fpliu.newton.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.HttpHeaders
import okhttp3.internal.platform.Platform
import okio.GzipSource
import okio.Okio
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

/**
 * 打印日志的拦截器
 *
 * @author 792793182@qq.com 2016-06-11
 */
class LogInterceptor(var logger: ((message: String) -> Unit)? = null) : Interceptor {

    init {
        if (logger == null) logger = { Platform.get().log(Platform.INFO, it, null) }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n--------------------------------------------------\n")

        val request = chain.request()
        val httpUrl = request.url()
        val url = httpUrl.url()

        stringBuilder.append(request.method()).append(" ").append(url.path)

        val query = url.query
        if (!isEmpty(query)) {
            stringBuilder.append('?').append(query)
        }
        stringBuilder.append(" HTTP/1.1").append("\n")

        if (isEmpty(request.header("Host"))) {
            stringBuilder.append("Host: ").append(url.host).append(":").append(httpUrl.port()).append("\n")
        }
        stringBuilder.append(request.headers().toString())

        val requestBody = request.body()

        if (requestBody != null) {
            val contentLength = requestBody.contentLength()
            if (contentLength != -1L && isEmpty(request.header("Content-Length"))) {
                stringBuilder.append("Content-Length: ").append(contentLength).append("\n")
            }

            val mediaType = requestBody.contentType()
            if (mediaType != null) {
                val contentType = mediaType.toString().toLowerCase(Locale.ENGLISH)

                if (isEmpty(request.header("Content-Length"))) {
                    stringBuilder.append("Content-Type: ").append(contentType).append("\n")
                }

                if (contentType.contains("application/json")
                        || contentType.contains("application/xml")
                        || contentType.contains("application/x-www-form-urlencoded")
                        || contentType.contains("text/html")) {
                    val bufferedSink = Okio.buffer(Okio.sink(ByteArrayOutputStream(contentLength.toInt())))
                    requestBody.writeTo(bufferedSink)
                    val content = bufferedSink.buffer().readString(Charset.forName("UTF-8"))
                    stringBuilder.append(content).append("\n")
                } else {
                    stringBuilder.append(requestBody)
                }
            }
        }

        stringBuilder.append("--------------------------------------------------\n")

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            throw RuntimeException(stringBuilder.toString(), e)
        }

        stringBuilder.append("HTTP/1.1 ").append(response.code()).append(" ").append(response.message()).append("\n")
                .append(response.headers().toString())

        val responseBody = response.body()

        if (responseBody != null) {
            val mediaType = responseBody.contentType()
            if (mediaType != null) {
                if ("gzip".equals(response.header("Content-Encoding")
                                ?: "", ignoreCase = true) && HttpHeaders.hasBody(response)) {
                    var source = responseBody.source()
                    source.request(java.lang.Long.MAX_VALUE)
                    source = Okio.buffer(GzipSource(source.buffer().clone()))
                    source.request(java.lang.Long.MAX_VALUE)
                    val content = source.buffer().clone().readString(Charset.forName("UTF-8"))
                    stringBuilder.append(content).append('\n')
                } else {
                    val contentType = mediaType.toString().toLowerCase(Locale.ENGLISH)
                    if (contentType.contains("application/json")
                            || contentType.contains("application/xml")
                            || contentType.contains("application/x-www-form-urlencoded")
                            || contentType.contains("text/html")) {
                        val source = responseBody.source()
                        // Buffer the entire body.
                        source.request(java.lang.Long.MAX_VALUE)
                        val buffer = source.buffer()
                        val content = buffer.clone().readString(Charset.forName("UTF-8"))
                        stringBuilder.append(content).append('\n')
                    } else {
                        stringBuilder.append(responseBody).append('\n')
                    }
                }
            }
        }
        stringBuilder.append("--------------------------------------------------\n")
        logger?.invoke(stringBuilder.toString())
        return response
    }

    private fun isEmpty(str: String?): Boolean = (null == str) || ("" == str)
}
