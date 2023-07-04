package com.example.myweather

import android.content.Context
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConcreteLocalSource private constructor(context: Context):LocalSource{
    private val weatherDAO: WeatherDAO = AppDatabase.getInstance(context).weatherDAO()
    override fun getWeatherFav(): Flow<List<WeatherResponse>?> {
        return weatherDAO.getFavWeather()
    }

    override fun insertIntoFav(weatherResponse: WeatherResponse) {
        weatherDAO.insertIntoFav(weatherResponse)
    }

    override suspend fun deleteFromFav(weatherResponse: WeatherResponse) {
        try {
            // Use a different coroutine scope for the database operation

            withContext(Dispatchers.IO) {
                weatherDAO.removeFromFav(weatherResponse)
            }

        } catch (e: Exception) {
            Log.d("the remove ls catch", e.message.toString())
        }
    }

    companion object {
        private  var instance: ConcreteLocalSource?=null
        fun getInstance(context: Context): ConcreteLocalSource {
            if (instance == null) {
                instance = ConcreteLocalSource(context)
            }
            return instance as ConcreteLocalSource
        }
    }
}