package com.example.myweather

import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface WeatherDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = WeatherResponse::class)
    fun insertIntoFav(weather: WeatherResponse)

    @Delete
    fun removeFromFav(weatherResponse: WeatherResponse)

    @Query("select * from FavWeatherData")
    fun getFavWeather(): Flow<List<WeatherResponse>>

    @Query("select * from AlertTable")
    fun getAlerts(): Flow<List<AlertPojo>>

    @Delete
    fun removeFromAlerts(alertPojo: AlertPojo)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = AlertPojo::class)
    fun insertIntoAlert(alertPojo: AlertPojo)

    @Query("select * from AlertTable where entryid = :entryId limit 1")
    fun getAlertWithId(entryId: String): AlertPojo

    @Query("update AlertTable set lat = :lat, lon= :lon where entryid = :entryId")
    fun updateAlertItemLatLongById(entryId: String, lat: Double, lon: Double)


}