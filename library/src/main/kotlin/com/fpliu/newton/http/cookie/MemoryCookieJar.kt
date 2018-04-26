package com.fpliu.newton.http.cookie

import java.util.ArrayList
import java.util.HashMap

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * 保存在内存中的Cookie
 *
 * @author 792793182@qq.com 2016-06-11
 */
class MemoryCookieJar : CookieJar {

    private val cookieStore = HashMap<HttpUrl, List<Cookie>>()

    override fun saveFromResponse(httpUrl: HttpUrl, list: List<Cookie>) {
        cookieStore[httpUrl] = list
    }

    override fun loadForRequest(httpUrl: HttpUrl): List<Cookie> {
        return cookieStore[httpUrl] ?: ArrayList()
    }
}
