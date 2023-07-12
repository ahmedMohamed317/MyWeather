package com.example.myweather.repo

import com.example.myweather.database.RepoInterface
import com.example.myweather.model.Alert
import com.example.myweather.model.AlertPojo
import com.example.myweather.model.CurrentWeather
import com.example.myweather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepo(
    private val alerts: MutableList<AlertPojo>,
    private val favs: MutableList<WeatherResponse>
) : RepoInterface {
    override suspend fun getWeather(
        lat: String,
        lon: String,
        units: String,
        lang: String
    ): WeatherResponse {

        return WeatherResponse(
           "1",
           lat.toDouble(),
           lon.toDouble(),
           mutableListOf(Alert("1", 0, "fog", "sender", 1, emptyList())),
           "",
           1,
           CurrentWeather(
               0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0, 0.0, emptyList()
           ),
           emptyList(),
           emptyList(),
           emptyList()

       )
    }

    override suspend fun getWeatherFav(): Flow<List<WeatherResponse>?> {
        return flowOf(favs)
    }

    override suspend fun insertIntoFav(weatherResponse: WeatherResponse) {
        favs.add(weatherResponse)
    }

    override suspend fun deleteFromFav(weatherResponse: WeatherResponse) {
        favs.remove(weatherResponse)
    }

    override suspend fun insertIntoAlerts(alertPojo: AlertPojo) {
        alerts.add(alertPojo)
    }

    override suspend fun removeFromAlerts(alertPojo: AlertPojo) {
        alerts.remove(alertPojo)
    }

    override suspend fun getAlerts(): Flow<List<AlertPojo>?> {
        return flowOf(alerts)
    }
}