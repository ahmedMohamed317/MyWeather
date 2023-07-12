package com.example.myweather.apistates

import com.example.myweather.model.AlertPojo


sealed class AlertApiState{
    class Success(var alertList: List<AlertPojo>?) : AlertApiState()

    class Failure(var throwable: String) : AlertApiState()

    object Loading : AlertApiState()

}