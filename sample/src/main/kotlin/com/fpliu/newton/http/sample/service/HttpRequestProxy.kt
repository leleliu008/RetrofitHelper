package com.fpliu.newton.http.sample.service

open class HttpRequestProxy(private val httpApi: HttpApi): HttpApi by httpApi