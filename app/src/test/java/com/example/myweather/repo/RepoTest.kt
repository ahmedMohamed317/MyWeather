package com.example.myweather.repo

import com.example.myweather.database.Repo
import com.example.myweather.fakedatasources.FakeLocalDataSourrce
import com.example.myweather.fakedatasources.FakeRemoteDataSource
import com.example.myweather.model.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class RepoTest {


    private lateinit var fakeLocalDataSource: FakeLocalDataSourrce
    private lateinit var fakeRemoteSource: FakeRemoteDataSource

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

    private lateinit var repo: Repo
    private val alertList = mutableListOf(alert1, alert2)
    private val favoriteList = mutableListOf(weatherResponse1, weatherResponse2)
    @Before
    fun setUp() {
        fakeLocalDataSource = FakeLocalDataSourrce(alertList, favoriteList)
        fakeRemoteSource = FakeRemoteDataSource(weatherResponse3)
        repo = Repo.getInstance(
            fakeRemoteSource, fakeLocalDataSource
        )
    }

    @After
    fun tearDown() {
    }


    @Test
    fun getFavoriteWeather_FavoriteList_SizeOfListEqualTwo() = runBlocking {

        // given : List contains Fav items

        //when : Getting favorite weather items

        //then : The size of the list of favorite = the size of favorite list attribute (2)

        repo.getWeatherFav().collect{

            if (it != null) {
                assertThat(it.size, CoreMatchers.`is`(2))
            }
        }


    }

    @Test
    fun addingFavoriteItem_addOneItem_sizeIncreasedToThree() = runBlocking {

        // given : insert a new favorite item
        repo.insertIntoFav(weatherResponse3)
        //when : Getting favorite weather items
        repo.getWeatherFav().collect{

            if (it != null) {
                assertThat(it.size, CoreMatchers.`is`(3))
            }

        }
        //then : The size of the list of favorite increase by one and become 3
    }

    @Test
    fun deleteFromFavorite_deletingOneItem_sizeDecreasedToOne()= runBlocking {

        // given : delete a new favorite item
        repo.deleteFromFav(weatherResponse2)
        //when : Getting favorite weather items
        repo.getWeatherFav().collect{

            if (it != null) {
                assertThat(it.size, CoreMatchers.`is`(1))
            }

        }
        //then : The size of the list of favorite decrease by one and become 1
    }
    @Test
    fun addingAlertItem_addOneItem_sizeIncreasedToThree() = runBlocking {

        // given : insert a new alert item
        repo.insertIntoAlerts(alert3)
        //when : Getting alert weather items
        repo.getAlerts().collect{

            if (it != null) {
                assertThat(it.size, CoreMatchers.`is`(3))
            }

        }
        //then : The size of the list of alerts  increase by one and become 3
    }

    @Test
    fun deleteFromAlerts_deletingOneItem_sizeDecreasedToOne()= runBlocking {

        // given : delete a new alert item
        repo.removeFromAlerts(alert2)
        //when : Getting alerts weather items
        repo.getAlerts().collect{

            if (it != null) {
                assertThat(it.size, CoreMatchers.`is`(1))
            }

        }
        //then : The size of the list of alerts decrease by one and become 1
    }

    @Test
    fun getAlertList_SizeOfListEqualTwo() = runBlocking {

        // given : List contains Alert items

        //when : Getting Alert weather items

        //then : The size of the list of Alerts =  (2)

        repo.getAlerts().collect{

            if (it != null) {
                assertThat(it.size, CoreMatchers.`is`(2))
            }
        }


    }
    @Test
    fun getAlertWithId_IDIsEqualOne() = runBlocking {

        // given : List contains Alert items

        //when : Getting Alert item with specific id = 1
        val alertPojo= repo.getAlertWithId("1")
        //then : the result = 1

        assertThat(alertPojo.id, CoreMatchers.`is`("1"))



    }
    @Test
    fun getWeatherData_returnLatEqualTen() = runBlocking {
        //Given : get new data with that arguments
        val weatherResponse = repo.getWeather("10", "10","metric","en")
        //then : data's returning lat is equal the argument passed 10.0 in double form
        if (weatherResponse != null) {
            assertThat(weatherResponse.lat, CoreMatchers.`is`(10.0))
        }

    }
}