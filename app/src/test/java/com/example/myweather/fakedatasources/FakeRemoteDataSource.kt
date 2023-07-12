package com.example.myweather.fakedatasources

import com.example.myweather.model.WeatherResponse
import com.example.myweather.network.RemoteSource
import retrofit2.Response

class FakeRemoteDataSource(var weatherResponse : WeatherResponse) : RemoteSource {
    override suspend fun getWeather(
        lat: String,
        lon: String,
        units: String,
        lang: String
    ): Response<WeatherResponse> {
        val response = weatherResponse.copy(lat = lat.toDouble(), lon = lon.toDouble())
        return Response.success(response)
    }
}