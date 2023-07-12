package com.example.myweather.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.myweather.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsFragment : Fragment(), OnMapReadyCallback,GoogleMap.OnMapClickListener {

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    private var floatingActionButton : FloatingActionButton? = null
    lateinit var longitude : String
    lateinit var latitude : String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        mapView = view.findViewById(R.id.mapView)
        floatingActionButton = view.findViewById(R.id.floatingActionButton)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        floatingActionButton?.setOnClickListener(View.OnClickListener {
            goToHome(longitude,latitude)
        })
        return view
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        googleMap?.setOnMapClickListener(this)

    }

    override fun onMapClick(latLng: LatLng) {
        if(floatingActionButton?.isVisible == false)
            floatingActionButton?.visibility = View.VISIBLE
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
    fun goToHome(long:String,lat:String) {
        val bundle = Bundle()
        bundle.putString("longitude", long)
        bundle.putString("latitude", lat)
        val fragment = HomeFragment()
        fragment.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fragment)?.commit()
    }
}
