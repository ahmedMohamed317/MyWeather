package com.example.myweather.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myweather.util.MainDispatcherRule
import com.example.myweather.apistates.FavApiState
import com.example.myweather.repo.FakeRepo
import com.example.myweather.ui.favorite.FavoriteViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test

class FavoriteViewModelTest {

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

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var fakeRepo: FakeRepo


    @Before
    fun setUp() {
        fakeRepo = FakeRepo(alertList,favoriteList)
        viewModel = FavoriteViewModel(fakeRepo)

    }


    @ExperimentalCoroutinesApi
    @Test
    fun whenGettingFavData_returnSizeEqualTwo() = runTest {
        viewModel.getFavWeather()
        //when : getting the favorite list

        //then : The size of the list of favorite = 2
        launch {
            viewModel.favWeatherFlow.collect {

                it as FavApiState.Success

                assertThat(it.favWeatherList!!.size, CoreMatchers.`is`(2))
                cancel()
            }
        }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun whenInsertingFavItem_returnSizeEqualThree() = runTest {
        //Given : a weather response objcet to be added to favorite list
        viewModel.insert(weatherResponse3)

        //when : adding the favorite pojo

        //then : The size of the list of favorite = 3
        launch {
            viewModel.favWeatherFlow.collect {

                it as FavApiState.Success

                assertThat(it.favWeatherList!!.size, CoreMatchers.`is`(3))
                cancel()
            }
        }
    }
    @ExperimentalCoroutinesApi
    @Test
    fun whenRemovingAlert_returnSizeEqualone() = runTest {
        viewModel.delete(weatherResponse2)
        //when : Deleting the favorite pojo

        //then : The size of the list of favorite = 1
        launch {
            viewModel.favWeatherFlow.collect {
                it as FavApiState.Success
                assertThat(it.favWeatherList!!.size, CoreMatchers.`is`(1))
                cancel()
            }
        }
    }
}