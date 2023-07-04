package com.example.myweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.requestLocationUpdates
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{
    lateinit var toolbar : Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toggle :ActionBarDrawerToggle
    lateinit var mFusedClient: FusedLocationProviderClient
    lateinit var longText: String
    lateinit var latText: String
    lateinit var mLastLocation: Location
    var isFirstTimeGps :Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        toggle = ActionBarDrawerToggle(this , drawerLayout,toolbar,R.string.open_nav,R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        mFusedClient = LocationServices.getFusedLocationProviderClient(this)

        showLocationMethodDialog()
//        toggle.syncState()
//        if (savedInstanceState == null)
//        {
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,HomeFragment()).commit()
//        }
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            mLastLocation  = locationResult.lastLocation!!
            longText = mLastLocation.latitude.toString()
            latText = mLastLocation.longitude.toString()
            Log.d("onMainActivitty" , longText+"  "+latText)
            val homeFragment = HomeFragment()
            val bundle = Bundle()
            bundle.putString("latitude", longText)
            bundle.putString("longitude", latText)
            if (isFirstTimeGps == true ) {
                homeFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    homeFragment
                ).commit()
                isFirstTimeGps = false
            }




        }
    }
    private fun requestPermissions() {
        val PERMISSION_ID = 20
        ActivityCompat.requestPermissions(

            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,

                ), PERMISSION_ID
        )

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(0)
        mFusedClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )

    }

    private fun checkPermissions(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        return result


    }

    private fun getLastLocation() {
        if (checkPermissions())
            if (isLocatedEnabled()) {
                requestNewLocationData()


            } else {
                Toast.makeText(this, "turn on your location ", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        else {

            requestPermissions()
        }


    }

    private fun isLocatedEnabled(): Boolean {

        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container,HomeFragment()).commit()
            R.id.nav_settings -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container,SettingsFragment()).commit()
            R.id.nav_fav -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container,FavoriteFragment()).commit()
            R.id.nav_alarm -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container,AlarmFragment()).commit()

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else{
            super.onBackPressed()
        }
    }
    private fun showLocationMethodDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Location Method")
        builder.setMessage("How do you want to get your location?")
        builder.setPositiveButton("GPS") { dialog, which ->
        getLastLocation()

        }
        builder.setNegativeButton("Map") { dialog, which ->
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MapsFragment()
            ).commit()
        }
        val dialog = builder.create()
        dialog.show()
    }
}