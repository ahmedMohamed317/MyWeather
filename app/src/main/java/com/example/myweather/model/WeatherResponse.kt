package com.example.myweather.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.myweather.database.TypeConverter
import java.util.*

@Entity(tableName = "FavWeatherData")
@TypeConverters(TypeConverter::class)

data class  WeatherResponse (
    @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString(),
    val lat: Double,
    val lon: Double,
    val alerts: List<Alert>?,
    val timezone: String,
    val timezone_offset: Int,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    var minutely: List<MinutelyWeather>,
    val daily: List<DailyWeather>


):java.io.Serializable