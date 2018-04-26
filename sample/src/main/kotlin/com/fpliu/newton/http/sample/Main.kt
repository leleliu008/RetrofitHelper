package com.fpliu.newton.http.sample

import com.fpliu.newton.http.RetrofitRequest
import com.fpliu.newton.http.interceptor.LogInterceptor
import com.fpliu.newton.http.sample.service.HttpRequest
import okhttp3.OkHttpClient

fun main(args: Array<String>) {
    RetrofitRequest.init(object : RetrofitRequest.AbstractConfig() {
        override fun getBaseUrl(): String {
            return "http://www.weather.com.cn"
        }

        override fun createOkHttpClientBuilder(): OkHttpClient.Builder {
            return super.createOkHttpClientBuilder().apply {
                addInterceptor(LogInterceptor())
            }
        }
    })

    HttpRequest
            .getWeatherInfo1()
            .subscribe({ println(it) }, { println(it) })

    HttpRequest
            .getWeatherInfo2()
            .subscribe({ println(it) }, { println(it) })
}