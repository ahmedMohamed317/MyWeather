package com.example.myweather.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.database.RepoInterface
import com.example.myweather.apistates.FavApiState
import com.example.myweather.model.WeatherResponse
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
//            Log.d("the remove button" , "in favmodel")

            iRepo.deleteFromFav(weatherResponse)
        }
    }

     fun getFavWeather()  {
        viewModelScope.launch {



            iRepo.getWeatherFav().collect{
            favWeatherFlow.emit(  FavApiState.Success(it))


            }
        }
    }

}

