package com.example.myweather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AlertViewModel(private val iRepo: RepoInterface) : ViewModel() {

    var alertWeatherFlow : MutableStateFlow<AlertApiState> = MutableStateFlow(AlertApiState.Loading)

    init {
        getAlertData()
    }



    fun insert(alertPojo: AlertPojo) {
        viewModelScope.launch {
            iRepo.insertIntoAlerts(alertPojo)

        }

    }

    fun delete(alertPojo: AlertPojo) {
        viewModelScope.launch {
            Log.d("the remove button" , "in alertmodel")

            iRepo.removeFromAlerts(alertPojo)
        }
    }

    private fun getAlertData()  {
        viewModelScope.launch {

            Log.d("in vm getAlertData" , "in launch")


            iRepo.getAlerts().collect{
                Log.d("in vm getAlertData" , "before emit")

                alertWeatherFlow.emit(  AlertApiState.Success(it))

                Log.d("in vm getAlertData" , "after emit")

            }
        }
    }

}

