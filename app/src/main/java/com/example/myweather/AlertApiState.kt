package com.example.myweather


sealed class AlertApiState{
    class Success(var alertList: List<AlertPojo>?) : AlertApiState()

    class Failure(var throwable: String) : AlertApiState()

    object Loading : AlertApiState()

}