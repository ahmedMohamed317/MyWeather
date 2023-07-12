package com.example.myweather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.database.RepoInterface
import com.example.myweather.apistates.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val iRepo: RepoInterface,
                    private val lat: String,
                    private val long: String,
                    private val units :String,
                    private val lang :String) : ViewModel() {

    val weatherResponseFlow : MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)


    init {
        viewModelScope.launch {
//            getAndEmitData(lat,long,units,lang)

        }

    }

    suspend fun getAndEmitData(lat: String,long: String,units: String,lang: String)
    {
        try {
//            Log.d("insidegetandemit", "inside try")

            val weatherResponse = iRepo.getWeather(lat,long,units,lang)
            weatherResponseFlow.emit(  ApiState.Success(weatherResponse))
        } catch (e: Exception) {
//            Log.d("insidegetandemitcattch", e.message.toString())
            weatherResponseFlow.emit(ApiState.Failure(e.message.toString()))
        }
    }


}