package com.example.myweather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val iRepo: RepoInterface) : ViewModel() {

    var favWeatherFlow : MutableStateFlow<FavApiState> = MutableStateFlow(FavApiState.Loading)

    init {
        getFavWeather()
    }



    fun insert(weatherResponse: WeatherResponse) {
        viewModelScope.launch {
            iRepo.insertIntoFav(weatherResponse)

        }

    }

    fun delete(weatherResponse: WeatherResponse) {
        viewModelScope.launch {
            Log.d("the remove button" , "in favmodel")

            iRepo.deleteFromFav(weatherResponse)
        }
    }

    private fun getFavWeather()  {
        viewModelScope.launch {



            iRepo.getWeatherFav().collect{
            favWeatherFlow.emit(  FavApiState.Success(it))


            }
        }
    }

}

