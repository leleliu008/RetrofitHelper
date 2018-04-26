package com.fpliu.newton.http.sample.service

import com.fpliu.newton.http.RetrofitRequest

object HttpRequest: HttpRequestProxy(RetrofitRequest.getRetrofit().create(HttpApi::class.java))