package com.example.myweather.model

data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
):java.io.Serializable
