package com.example.myweather.fakedatasources

import com.example.myweather.database.LocalSource
import com.example.myweather.model.AlertPojo
import com.example.myweather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSourrce (
    private val alertsList: MutableList<AlertPojo>,
    private val favoriteList: MutableList<WeatherResponse>
) : LocalSource {
    override fun getWeatherFav(): Flow<List<WeatherResponse>?> {
        return flowOf(favoriteList)
    }

    override fun insertIntoFav(weatherResponse: WeatherResponse) {
        favoriteList.add(weatherResponse)
    }

    override suspend fun deleteFromFav(weatherResponse: WeatherResponse) {
        favoriteList.remove(weatherResponse)
    }

    override suspend fun insertIntoAlert(alertPojo: AlertPojo) {
        alertsList.add(alertPojo)
    }

    override suspend fun removeFromAlerts(alertPojo: AlertPojo) {
        alertsList.remove(alertPojo)
    }

    override fun getAlerts(): Flow<List<AlertPojo>> {
        return flowOf(alertsList)
    }

    override fun getAlertWithId(entryId: String): AlertPojo {
        return alertsList.first { it.id == entryId }
    }

    override fun updateAlertItemLatLongById(entryId: String, lat: Double, lon: Double) {
        for (alert in alertsList){
            if (alert.id==entryId)
            {
                alert.lat=lat
                alert.lon=lon
                break
            }

        }

    }
}