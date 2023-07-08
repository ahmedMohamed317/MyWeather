package com.example.myweather

import kotlinx.coroutines.flow.Flow

interface LocalSource {

    fun getWeatherFav(): Flow<List<WeatherResponse>?>
    fun insertIntoFav(weatherResponse: WeatherResponse)
    suspend fun deleteFromFav(weatherResponse: WeatherResponse)
    suspend fun insertIntoAlert(alertPojo: AlertPojo )
    suspend fun removeFromAlerts(alertPojo: AlertPojo)
    fun getAlerts(): Flow<List<AlertPojo>>
    fun getAlertWithId(entryId: String): AlertPojo
    fun updateAlertItemLatLongById(entryId: String, lat: Double, lon: Double)


}