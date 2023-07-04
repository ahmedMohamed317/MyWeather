package com.example.myweather

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("data/2.5/onecall")
    suspend fun getWeather(

        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String
    ): Response<WeatherResponse>


//
//        @GET("data/2.5/onecall?lat=33.44&lon=-94.04&appid=68a39f5322e1e6f73d05d56cad1c1dd5")
//        suspend fun getWeather(
////            @Query("lat") lat: String,
////            @Query("lon") lon: String,
////            @Query("units") units: String,
////            @Query("appid") apiKey: String,
////            @Query("lang") lang: String
//        ): Response<WeatherResponse>
}
