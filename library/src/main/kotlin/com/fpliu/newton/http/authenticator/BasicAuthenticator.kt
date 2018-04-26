package com.fpliu.newton.http.authenticator

import okhttp3.*

/**
 * 基本认证
 *
 * @author 792793182@qq.com 2016-06-11
 */
open class BasicAuthenticator(private val username: String,
                              private val password: String) : Authenticator {

    override fun authenticate(route: Route, response: Response): Request {
        val credential = Credentials.basic(username, password)
        return response.request().newBuilder().header("Authorization", credential).build()
    }
}
