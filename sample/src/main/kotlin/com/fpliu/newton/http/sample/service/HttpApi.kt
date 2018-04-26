package com.fpliu.newton.http.sample.service

import com.fpliu.newton.http.sample.entity.WeatherInfoEntity
import io.reactivex.Observable
import retrofit2.http.GET

interface HttpApi {

    @GET("/data/cityinfo/101010100.html")
    fun getWeatherInfo1(): Observable<String>

    @GET("/data/cityinfo/101010100.html")
    fun getWeatherInfo2(): Observable<WeatherInfoEntity>
}