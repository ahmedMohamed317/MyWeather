package com.example.myweather

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response


class Repo private constructor(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) : RepoInterface {

    override suspend fun getWeather(lat: String, lon: String , units : String , lang:String) : WeatherResponse? {
        Log.d("TAGRepoooooo",lang+" "+units)
        return remoteSource.getWeather(lat,lon,units,lang).body()
    }

    override suspend fun getWeatherFav(): Flow<List<WeatherResponse>?> {
        return localSource.getWeatherFav()
    }

    override suspend fun insertIntoFav(weatherResponse: WeatherResponse) {
        localSource.insertIntoFav(weatherResponse)
    }

    override suspend fun deleteFromFav(weatherResponse: WeatherResponse) {
        Log.d("the remove button" , "in repo")

        localSource.deleteFromFav(weatherResponse)
    }

    override suspend fun insertIntoAlerts(alertPojo: AlertPojo) {
        localSource.insertIntoAlert(alertPojo)
    }

    override suspend fun removeFromAlerts(alertPojo: AlertPojo) {
        localSource.removeFromAlerts(alertPojo)
    }

    override suspend fun getAlerts(): Flow<List<AlertPojo>?> {
        try {
            Log.d("in repo try" ,"get alerts")


        return localSource.getAlerts()
        }
        catch (e:Exception){
            Log.d("in repo getAlertcatch" , e.message.toString())

        }
        return localSource.getAlerts()
    }

    fun getAlertWithId(entryId: String): AlertPojo {
        return localSource.getAlertWithId(entryId)
    }


    companion object {
        private var instance: Repo? = null

        fun getInstance(remoteSource: RemoteSource , localSource: LocalSource): Repo {
            if (instance == null) {
                instance = Repo(remoteSource, localSource)
            }
            return instance as? Repo ?: throw IllegalStateException("Instance is null ")
        }
    }
}