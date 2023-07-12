package com.example.myweather.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myweather.util.MainDispatcherRule
import com.example.myweather.apistates.ApiState
import com.example.myweather.repo.FakeRepo
import com.example.myweather.ui.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test

class HomeViewModelTest {


    private val alertList = mutableListOf<AlertPojo>()
    private val favoriteList = mutableListOf<WeatherResponse>()

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeRepo: FakeRepo


    @Before
    fun setUp() {
        fakeRepo = FakeRepo(alertList,
            favoriteList
        )
        viewModel = HomeViewModel(fakeRepo,"","","","")

    }


    @ExperimentalCoroutinesApi
    @Test
    fun whenGettingFavData_returnSizeEqualTwo() = runTest {
        viewModel.getAndEmitData("1.0","1.0","metric","")
        //when : getting the data of weather response with specific lat = 1

        //then : The latitude of the object = 1
        launch {
            viewModel.weatherResponseFlow.collect {

                it as ApiState.Success
                it.weatherResponse?.lat
                assertThat(it.weatherResponse!!.lat, CoreMatchers.`is`(1.0))
                cancel()
            }
        }
    }
}