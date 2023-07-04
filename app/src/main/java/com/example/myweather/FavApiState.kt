package com.example.myweather

sealed class FavApiState{
    class Success(var favWeatherList: List<WeatherResponse>?) : FavApiState()

    class Failure(var throwable: String) : FavApiState()

    object Loading : FavApiState()

}
