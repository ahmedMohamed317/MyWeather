package com.example.myweather.database

import android.content.Context
import android.util.Log
import com.example.myweather.model.AlertPojo
import com.example.myweather.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ConcreteLocalSource private constructor(context: Context): LocalSource {
    private val weatherDAO: WeatherDAO = AppDatabase.getInstance(context).weatherDAO()
    override fun getWeatherFav(): Flow<List<WeatherResponse>?> {
        return weatherDAO.getFavWeather()

    }

    override fun insertIntoFav(weatherResponse: WeatherResponse) {
        try {


                weatherDAO.insertIntoFav(weatherResponse)
                Log.d("the add ls try", "e.message.toString()")



        } catch (e: Exception) {
            Log.d("the add ls catch", e.message.toString())
        }
    }

    override suspend fun deleteFromFav(weatherResponse: WeatherResponse) {
        try {

            withContext(Dispatchers.IO) {
                weatherDAO.removeFromFav(weatherResponse)
            }

        } catch (e: Exception) {
            Log.d("the remove ls catch", e.message.toString())
        }
    }

    override suspend fun insertIntoAlert(alertPojo: AlertPojo) {

        try {

            withContext(Dispatchers.IO) {
                weatherDAO.insertIntoAlert(alertPojo)
            }

        } catch (e: Exception) {
            Log.d("the inseralert ln catch", e.message.toString())
        }
    }

    override suspend fun removeFromAlerts(alertPojo: AlertPojo) {
        try {

            withContext(Dispatchers.IO) {
                weatherDAO.removeFromAlerts(alertPojo)
            }

        } catch (e: Exception) {
            Log.d("the remove ls catch", e.message.toString())
        }

    }

    override fun getAlerts(): Flow<List<AlertPojo>> {
        try {
            Log.d("the getAlertLS", "before method")

            return weatherDAO.getAlerts()
        }
        catch (e : Exception){
            Log.d("the getAlertsCatchLs", e.message.toString())

        }
        return weatherDAO.getAlerts()

    }

    override fun getAlertWithId(entryId: String): AlertPojo {
        return weatherDAO.getAlertWithId(entryId)
    }

    override fun updateAlertItemLatLongById(entryId: String, lat: Double, lon: Double) {
        weatherDAO.updateAlertItemLatLongById(entryId, lat, lon)
    }
    fun getFavWithId(entryId: String): WeatherResponse {
        return weatherDAO.getFavWeatherWithId(entryId)
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