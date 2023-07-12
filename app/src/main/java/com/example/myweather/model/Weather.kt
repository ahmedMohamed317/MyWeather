package com.example.myweather.model

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
):java.io.Serializable