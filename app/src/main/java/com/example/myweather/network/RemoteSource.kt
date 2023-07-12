package com.example.myweather.network

import com.example.myweather.model.City
import com.example.myweather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteSource {

    suspend fun getWeather(lat: String, lon: String , units : String , lang:String): Response<WeatherResponse>
    suspend fun getAutoComplete(city: String): Response<List<City>>

}