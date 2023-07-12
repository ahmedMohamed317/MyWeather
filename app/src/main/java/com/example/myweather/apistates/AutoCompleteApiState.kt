package com.example.myweather.apistates

import com.example.myweather.model.AlertPojo
import com.example.myweather.model.City

sealed class AutoCompleteApiState {


    class Success(var city: List<City>?) : AutoCompleteApiState()
    class Failure(var throwable: String) : AutoCompleteApiState()

    object Loading : AutoCompleteApiState()


}