package com.example.myweather.ui.favorite

import com.example.myweather.model.WeatherResponse

interface OnDeleteFavoriteInterface {

    fun onFavoriteDelete(weatherResponse: WeatherResponse)

}