package com.example.myweather.apistates

import com.example.myweather.model.WeatherResponse

sealed class FavApiState{
    class Success(var favWeatherList: List<WeatherResponse>?) : FavApiState()

    class Failure(var throwable: String) : FavApiState()

    object Loading : FavApiState()

}
