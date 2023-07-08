package com.example.myweather

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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

class AlertMapActivity : AppCompatActivity() , OnMapReadyCallback, GoogleMap.OnMapClickListener{


    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    private lateinit var floatingActionButton : FloatingActionButton
    var longitude : Double = 0.0
    var latitude : Double = 0.0
    var id :String? = null
    lateinit var localSource : ConcreteLocalSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getStringExtra("ID")
        Log.d("id value in map" , id.toString()
        )

        setContentView(R.layout.activity_favorite_map)
        floatingActionButton = findViewById(R.id.floatingActionButtonSave)
        mapView =findViewById(R.id.mapViewAc)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        localSource = ConcreteLocalSource.getInstance(applicationContext)
        floatingActionButton.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO){
                id?.let { it1 -> localSource.updateAlertItemLatLongById(it1,latitude,longitude)
                    Log.d("id value in map" , it1)
                }
                finish()
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
        latitude = latLng.latitude
        longitude = latLng.longitude
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


}
