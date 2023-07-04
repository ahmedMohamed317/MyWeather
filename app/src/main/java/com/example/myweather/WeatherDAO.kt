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
}