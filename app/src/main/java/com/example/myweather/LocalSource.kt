package com.example.myweather

import kotlinx.coroutines.flow.Flow

interface LocalSource {

    fun getWeatherFav(): Flow<List<WeatherResponse>?>
    fun insertIntoFav(weatherResponse: WeatherResponse)
    suspend fun deleteFromFav(weatherResponse: WeatherResponse)


}