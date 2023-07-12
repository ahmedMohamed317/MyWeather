package com.example.myweather.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myweather.util.MainDispatcherRule
import com.example.myweather.apistates.AlertApiState
import com.example.myweather.repo.FakeRepo
import com.example.myweather.ui.alert.AlertViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AlertViewModelTest {

    private val weatherResponse1 = WeatherResponse(
        "1",
        0.0,
        0.0,
        mutableListOf(Alert("1", 0, "fog","sender",1, emptyList())),
        "",
        1,
        CurrentWeather(
            0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0, 0.0, emptyList()
        ),
        emptyList(),
        emptyList(),
        emptyList()

    )

    private val weatherResponse2 = WeatherResponse(
        "2",
        0.0,
        0.0,
        mutableListOf(Alert("2", 0, "fog","sender",1, emptyList())),
        "",
        1,
        CurrentWeather(
            0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0, 0.0, emptyList()
        ),
        emptyList(),
        emptyList(),
        emptyList()

    )

    private val weatherResponse3 = WeatherResponse(
        "3",
        0.0,
        0.0,
        mutableListOf(Alert("3", 0, "wind","sender",1, emptyList())),
        "",
        1,
        CurrentWeather(
            0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0, 0.0, emptyList()
        ),
        emptyList(),
        emptyList(),
        emptyList()

    )

    private val alert1 = AlertPojo(
        "1",1,2, AlertKind.ALARM)

    private val alert2 = AlertPojo(
        "2",3,4, AlertKind.NOTIFICATION)

    private val alert3 = AlertPojo(
        "3",3,4, AlertKind.NOTIFICATION)

    private val alertList = mutableListOf(alert1, alert2)
    private val favoriteList = mutableListOf(weatherResponse1, weatherResponse2)

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AlertViewModel
    private lateinit var fakeRepo: FakeRepo


    @Before
    fun setUp() {
        fakeRepo = FakeRepo(alertList,favoriteList)
        viewModel = AlertViewModel(fakeRepo)

    }

    @ExperimentalCoroutinesApi
    @Test
    fun whenGettingAlertData_returnSizeEqualTwo() = runTest {
        //when : Getting alert weather Data

        viewModel.getAlertData()

        //then : The size of the list of alerts = 2
        launch {
            viewModel.alertWeatherFlow.collect {

                it as AlertApiState.Success

                assertThat(it.alertList!!.size, CoreMatchers.`is`(2))
                cancel()
            }
        }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun whenInsertingAlert_returnSizeEqualThree() = runTest {
        //Given : alertPojo to be added to the list

        val alertPojo = AlertPojo("3",1,2, AlertKind.NOTIFICATION,0.0,0.0)
        //when : Adding the alert pojo

        //then : The size of the list of alerts = 3
        viewModel.insert(alertPojo)
        launch {
            viewModel.alertWeatherFlow.collect {

                it as AlertApiState.Success

                assertThat(it.alertList!!.size, CoreMatchers.`is`(3))
                cancel()
            }
        }
    }
    @ExperimentalCoroutinesApi
    @Test
    fun whenRemovingAlert_returnSizeEqualone() = runTest {
        //when : Deleting the alert pojo

        viewModel.delete(alert2)

        //then : The size of the list of alerts = 1
        launch {
            viewModel.alertWeatherFlow.collect {
                it as AlertApiState.Success
                assertThat(it.alertList!!.size, CoreMatchers.`is`(1))
                cancel()
            }
        }
    }
}