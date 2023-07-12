package com.example.myweather.database

import com.example.myweather.model.AlertPojo
import com.example.myweather.model.City
import com.example.myweather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface RepoInterface {

    suspend fun getWeather(lat: String, lon: String , units : String , lang:String): WeatherResponse?
    suspend fun getWeatherFav(): Flow<List<WeatherResponse>?>
    suspend fun insertIntoFav(weatherResponse: WeatherResponse)
    suspend fun deleteFromFav(weatherResponse: WeatherResponse)
    suspend fun insertIntoAlerts(alertPojo: AlertPojo)
    suspend fun removeFromAlerts(alertPojo: AlertPojo)
    suspend fun getAlerts(): Flow<List<AlertPojo>?>
    suspend fun getAutoComplete(city: String): List<City>?

}