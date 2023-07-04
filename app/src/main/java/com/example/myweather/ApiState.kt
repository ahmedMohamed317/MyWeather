package com.example.myweather

sealed class ApiState {
    class Success(var weatherResponse: WeatherResponse?) : ApiState()

    class Failure(var throwable: String) : ApiState()

    object Loading : ApiState()
}
