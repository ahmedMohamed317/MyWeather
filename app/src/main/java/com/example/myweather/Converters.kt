package com.example.myweather
import androidx.room.TypeConverter
import com.example.myweather.CurrentWeather
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCurrentWeather(current: CurrentWeather): String {
        return gson.toJson(current)
    }

    @TypeConverter
    fun toCurrentWeather(json: String): CurrentWeather {
        return gson.fromJson(json, CurrentWeather::class.java)
    }
}
