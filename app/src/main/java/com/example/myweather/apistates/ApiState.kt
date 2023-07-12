package com.example.myweather.apistates

import com.example.myweather.model.WeatherResponse

sealed class ApiState {
    class Success(var weatherResponse: WeatherResponse?) : ApiState()

    class Failure(var throwable: String) : ApiState()

    object Loading : ApiState()
}
