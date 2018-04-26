package com.fpliu.newton.http.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 为请求添加请求头的拦截器
 *
 * @author 792793182@qq.com 2017-06-23.
 */
abstract class RequestInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return if (filter(request)) {
            val newRequestBuilder = request.newBuilder()
            config(request, newRequestBuilder)
            chain.proceed(newRequestBuilder.build())
        } else {
            chain.proceed(request)
        }
    }

    open fun filter(request: Request): Boolean = true

    abstract fun config(originRequest: Request, newRequestBuilder: Request.Builder)
}