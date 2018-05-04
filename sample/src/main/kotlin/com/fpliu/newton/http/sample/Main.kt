package com.fpliu.newton.http.sample

import com.fpliu.newton.http.RetrofitRequest
import com.fpliu.newton.http.download.DownloadInfoPersistent2File
import com.fpliu.newton.http.download.Downloader
import com.fpliu.newton.http.interceptor.LogInterceptor
import com.fpliu.newton.http.sample.service.HttpRequest
import okhttp3.OkHttpClient
import java.io.File

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

    //下载设置：设置下载的文件的元信息存放位置，也可以自己实现如何存在这些元信息，非必须，有默认的设置
    Downloader.downloadInfoPersistent = DownloadInfoPersistent2File("/Users/leleliu008/hahahaha/")

    Downloader.downloadSync("https://www.baidu.com/", File("/Users/leleliu008/hahahaha.txt"))
}