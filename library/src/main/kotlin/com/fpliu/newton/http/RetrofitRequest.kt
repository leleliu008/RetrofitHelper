package com.fpliu.newton.http

import com.fpliu.newton.http.converter.StringConverterFactory
import com.fpliu.newton.http.cookie.MemoryCookieJar
import okhttp3.OkHttpClient
import okhttp3.internal.http.BridgeInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author 792793182@qq.com 2016-06-11
 */
object RetrofitRequest {

    private var config: Config? = null

    private val retrofitMap = HashMap<String, Retrofit>()

    fun init(config: Config) {
        RetrofitRequest.config = config
    }

    fun getRetrofit(baseUrl: String? = null): Retrofit {
        var localBaseUrl = if (baseUrl == null || baseUrl == "") {
            config!!.getBaseUrl()
        } else {
            baseUrl!!
        }
        var retrofit: Retrofit? = retrofitMap[localBaseUrl]
        if (retrofit == null) {
            retrofit = config!!.createRetrofitBuilder(config!!.createOkHttpClientBuilder().build(), localBaseUrl).build()
            retrofitMap[localBaseUrl] = retrofit
        }
        return retrofit!!
    }

    interface Config {

        fun getBaseUrl(): String

        fun createOkHttpClientBuilder(): OkHttpClient.Builder

        fun createRetrofitBuilder(okHttpClient: OkHttpClient, baseUrl: String): Retrofit.Builder
    }

    abstract class AbstractConfig : Config {

        override fun createOkHttpClientBuilder(): OkHttpClient.Builder {
            val cookieJar = MemoryCookieJar()
            return OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)//设置链接超时时间
                    .readTimeout(10, TimeUnit.SECONDS)   //设置读取超时时间
                    .writeTimeout(10, TimeUnit.SECONDS)  //设置写入超时时间
                    .retryOnConnectionFailure(true)      //设置失败后重试
                    .addInterceptor(BridgeInterceptor(cookieJar))
                    .cookieJar(cookieJar)
        }

        override fun createRetrofitBuilder(okHttpClient: OkHttpClient, baseUrl: String): Retrofit.Builder {
            return Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseUrl)
                    .addConverterFactory(StringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }
    }
}