package com.example.myweather

import retrofit2.Response

interface RemoteSource {

    suspend fun getWeather(lat: String, lon: String , units : String , lang:String): WeatherResponse?
}