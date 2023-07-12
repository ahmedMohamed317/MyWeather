package com.example.myweather.ui.favorite

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myweather.R
import com.example.myweather.database.Repo
import com.example.myweather.apistates.ApiState
import com.example.myweather.database.ConcreteLocalSource
import com.example.myweather.network.ApiClient
import com.example.myweather.ui.home.HomeViewModel
import com.example.myweather.ui.home.HomeViewModleFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class FavoriteMapActivity : AppCompatActivity() , OnMapReadyCallback, GoogleMap.OnMapClickListener{


    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    private lateinit var floatingActionButton : FloatingActionButton
    lateinit var longitude : String
    lateinit var latitude : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_alert_map)
        floatingActionButton = findViewById(R.id.floatingActionButtonSaveAlert)
        mapView =findViewById(R.id.mapViewAcAlert)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        floatingActionButton.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO){
                getAndSaveData()
//                var weatherResponse:WeatherResponse?= ApiClient().getWeather(latitude,longitude,"metric","en").body()
//                if (weatherResponse != null) {
//                    ConcreteLocalSource.getInstance(applicationContext).insertIntoFav(weatherResponse)
//                    finish()
//                }
            }

        }
    }
    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        googleMap?.setOnMapClickListener(this)

    }

    override fun onMapClick(latLng: LatLng) {
        if(floatingActionButton.isVisible == false)
            floatingActionButton.visibility = View.VISIBLE
        latitude = latLng.latitude.toString()
        longitude = latLng.longitude.toString()
        Log.d("lat",longitude.toString())
        Log.d("lat",latitude.toString())
        currentMarker?.remove()
        currentMarker = googleMap?.addMarker(MarkerOptions().position(latLng))


    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    suspend fun getAndSaveData(){
        Log.d("inside getandsavedata", "beforeDataemitted}")
        try {

            val favoriteViewModelFactory = FavoriteViewModelFactory(
                Repo.getInstance(ApiClient(), ConcreteLocalSource.getInstance(applicationContext)))
            var favoriteViewModel = ViewModelProvider(this, favoriteViewModelFactory).get(
                FavoriteViewModel::class.java)
            val homeViewModelFactory = HomeViewModleFactory(
                Repo.getInstance(ApiClient(), ConcreteLocalSource.getInstance(applicationContext)),latitude,longitude,"metric","en")
            val viewModelHome = ViewModelProvider(this,homeViewModelFactory).get(HomeViewModel::class.java)


//
            ApiClient().getWeather(latitude,longitude,"metric","en")
        viewModelHome.getAndEmitData(latitude,longitude,"metric","en")
            Log.d("inside getandsavedata", "Dataemitted}")

            viewModelHome.weatherResponseFlow.collectLatest { status ->

                Log.d("insideflow", "onView: ${status.javaClass}")
                when (status) {
                    is ApiState.Loading -> {
                        Log.d("insideLoading", "onView: ${status.javaClass}")

                    }
                    is ApiState.Success -> {
                        var id =  UUID.randomUUID()
                        status.weatherResponse?.let {
                            val weatherResponse = it.copy(minutely = it.minutely ?: emptyList(),id = it.id ?: id.toString())
                            Log.d("before insert", weatherResponse.id)
                            ConcreteLocalSource.getInstance(applicationContext).insertIntoFav(weatherResponse)
                            Log.d("after insert", "status: Success")
                            finish()
                        } ?: run {
                            Log.d(ContentValues.TAG, "status: Fail")
                            finish()

                        }


                    }
                    else -> {
                        Log.d("insideElse", "onView: ${status.javaClass}")

                    }
                }
            }






        }
        catch (e:Exception){
            Log.d("inside catch gasd", e.message.toString())

        }



    }



}
