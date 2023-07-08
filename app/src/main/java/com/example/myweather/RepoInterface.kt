package com.example.myweather

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response

interface RepoInterface {

    suspend fun getWeather(lat: String, lon: String , units : String , lang:String): WeatherResponse?
    suspend fun getWeatherFav(): Flow<List<WeatherResponse>?>
    suspend fun insertIntoFav(weatherResponse: WeatherResponse)
    suspend fun deleteFromFav(weatherResponse: WeatherResponse)
    suspend fun insertIntoAlerts(alertPojo: AlertPojo)
    suspend fun removeFromAlerts(alertPojo: AlertPojo)
    suspend fun getAlerts(): Flow<List<AlertPojo>?>

}