package com.example.myweather

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class TypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromCurrentWeather(current: CurrentWeather): String {
        return gson.toJson(current)
    }

    @TypeConverter
    fun toCurrentWeather(json: String): CurrentWeather {
        return gson.fromJson(json, CurrentWeather::class.java)
    }

    @TypeConverter
    fun fromMinutelyWeatherList(minutely: List<MinutelyWeather>): String {
        return gson.toJson(minutely)
    }

    @TypeConverter
    fun toMinutelyWeatherList(json: String): List<MinutelyWeather> {
        val listType = object : TypeToken<List<MinutelyWeather>>() {}.type
        return gson.fromJson(json, listType)
    }

    @TypeConverter
    fun fromHourlyWeatherList(hourly: List<HourlyWeather>): String {
        return gson.toJson(hourly)
    }

    @TypeConverter
    fun toHourlyWeatherList(json: String): List<HourlyWeather> {
        val listType = object : TypeToken<List<HourlyWeather>>() {}.type
        return gson.fromJson(json, listType)
    }

    @TypeConverter
    fun fromDailyWeatherList(daily: List<DailyWeather>): String {
        return gson.toJson(daily)
    }

    @TypeConverter
    fun toDailyWeatherList(json: String): List<DailyWeather> {
        val listType = object : TypeToken<List<DailyWeather>>() {}.type
        return gson.fromJson(json, listType)
    }
    @TypeConverter
    fun fromJsonString(json: String?): List<Alert>? {
        val listType = object : TypeToken<List<Alert>?>() {}.type

        return gson.fromJson(json, listType)
    }

    @TypeConverter
    fun toJsonString(alerts: List<Alert>?): String? {
        return gson.toJson(alerts)
    }
//    @TypeConverter
//    fun fromWeatherDescriptionList(descriptions: List<WeatherDescription>): String {
//        return gson.toJson(descriptions)
//    }
//
//    @TypeConverter
//    fun toWeatherDescriptionList(json: String): List<WeatherDescription> {
//        val listType = object : TypeToken<List<WeatherDescription>>() {}.type
//        return gson.fromJson(json, listType)
//    }
}


