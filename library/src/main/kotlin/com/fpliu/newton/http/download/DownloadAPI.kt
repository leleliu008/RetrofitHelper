package com.fpliu.newton.http.download

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * 下载相关的API
 * @author 792793182@qq.com 2018-04-25.
 */
interface DownloadAPI {

    @GET
    @Streaming
    fun download(@Url url: String): Call<ResponseBody>

    @GET
    @Streaming
    fun download(@Url url: String, @Header("If-None-Match") eTag: String): Call<ResponseBody>

    @GET
    @Streaming
    fun continueDownload(@Url url: String, @Header("Range") range: String): Call<ResponseBody>

    @GET
    @Streaming
    fun continueDownload(@Url url: String, @Header("If-None-Match") eTag: String, @Header("Range") range: String): Call<ResponseBody>
}