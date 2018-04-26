package com.fpliu.newton.http.sample.entity

data class WeatherInfoEntity(val weatherinfo: WeatherInfo) {
    data class WeatherInfo(val city: String,
                           val cityid: String,
                           val temp1: String,
                           val temp2: String,
                           val weather: String,
                           val img1: String,
                           val img2: String,
                           val ptime: String)
}