package com.example.myweather.ui.favorite

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.databinding.ActivityFavoriteDetailsBinding
import com.example.myweather.databinding.FragmentHomeBinding
import com.example.myweather.model.WeatherResponse
import com.example.myweather.ui.home.DailyAdapter
import com.example.myweather.ui.home.HourlyAdapter
import com.example.myweather.ui.settings.changeTimeZoneToTime
import com.example.myweather.ui.settings.changeTimeZoneToTimestamp
import com.example.myweather.ui.settings.setTemp
import com.example.myweather.ui.settings.setWindSpeed
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class FavoriteDetailsActivity : AppCompatActivity() {
    lateinit var pojo : WeatherResponse
    lateinit var binding:FragmentHomeBinding
    lateinit var dailyAdapter : DailyAdapter
    lateinit var hourlyAdapter : HourlyAdapter
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences =applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        pojo = intent.getSerializableExtra("EXTRA_OBJECT") as WeatherResponse
        setDataToViews(pojo)

        dailyAdapter = DailyAdapter(applicationContext)
        hourlyAdapter = HourlyAdapter(applicationContext)
        binding.recyclerViewHours.adapter = hourlyAdapter
        binding.recyclerViewDays.adapter = dailyAdapter
        dailyAdapter.submitList(pojo.daily)
        hourlyAdapter.submitList(pojo.hourly)


    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun setDataToViews(weatherResponse: WeatherResponse) {

        if(isDayTime(changeTimeZoneToTimestamp(weatherResponse.timezone),weatherResponse.current.sunrise,weatherResponse.current.sunset)
            && (weatherResponse.current.weather[0].main == "Clear"))
            binding.mainLayout.setBackgroundResource(R.drawable.clear_day_bg)
        else if(!isDayTime(changeTimeZoneToTimestamp(weatherResponse.timezone),weatherResponse.current.sunrise,weatherResponse.current.sunset)
            && (weatherResponse.current.weather[0].main == "Clear"))
            binding.mainLayout.setBackgroundResource(R.drawable.clear_night_bg)
        else if(isDayTime(changeTimeZoneToTimestamp(weatherResponse.timezone),weatherResponse.current.sunrise,weatherResponse.current.sunset)
            && (weatherResponse.current.weather[0].main == "Rain"))
            binding.mainLayout.setBackgroundResource(R.drawable.rain_day_bg)
        else if(!isDayTime(changeTimeZoneToTimestamp(weatherResponse.timezone),weatherResponse.current.sunrise,weatherResponse.current.sunset)
            && (weatherResponse.current.weather[0].main == "Rain"))
            binding.mainLayout.setBackgroundResource(R.drawable.rain_night_bg)
        else if(isDayTime(changeTimeZoneToTimestamp(weatherResponse.timezone),weatherResponse.current.sunrise,weatherResponse.current.sunset)
        )
            binding.mainLayout.setBackgroundResource(R.drawable.cloud_day_bg)
        else if(!isDayTime(changeTimeZoneToTimestamp(weatherResponse.timezone),weatherResponse.current.sunrise,weatherResponse.current.sunset)
        )
            binding.mainLayout.setBackgroundResource(R.drawable.cloud_night_bg)


        binding.sunriseTv.text = getFormattedTime(weatherResponse.current.sunrise,weatherResponse.timezone)
        binding.sunsetTv.text = getFormattedTime(weatherResponse.current.sunset,weatherResponse.timezone)
        binding.longTv.text = weatherResponse.lon.toInt().toString()
        binding.latTv.text = weatherResponse.lat.toInt().toString()
        binding.cloudTv.text = weatherResponse.current.clouds.toString().plus(" %")
        binding.pressureTv.text = weatherResponse.current.pressure.toString().plus(" atm")
        binding.uvTv.text = weatherResponse.current.uvi.toString()
        binding.rainTv.text = weatherResponse.current.humidity.toString().plus(" %")
        binding.visibilityTv.text = weatherResponse.current.visibility.toString().plus(" m")
        binding.windtTv.setWindSpeed(weatherResponse.current.wind_speed,sharedPreferences)
        binding.txtViewLocation.text = getCountryFromLatLng(weatherResponse.lat,weatherResponse.lon)
        binding.txtViewAddrress.text = getAddressFromLatLng(weatherResponse.lat,weatherResponse.lon)
        binding.textViewDate.text =getFormattedDate( weatherResponse.current.dt)
        binding.txtViewTemperatureDegree.setTemp(weatherResponse.current.temp.toInt(),sharedPreferences)
        val iconString = weatherResponse.current.weather[0].icon
        val urlString = "https://openweathermap.org/img/wn/$iconString@4x.png"
        Glide.with(applicationContext)
            .load(urlString)
            .into(binding.imageViewWeatherIcon)
        binding.txtViewWeatherCondition.text = weatherResponse.current.weather[0].main
        binding.descriptionTv.text = weatherResponse.current.weather[0].description
        if (weatherResponse.timezone!="Africa/Cairo")
            binding.textViewhour.text = changeTimeZoneToTime(weatherResponse.timezone)
        else
            binding.textViewhour.text =getFormattedTime( weatherResponse.current.dt)




    }





    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDate(dt:Long): String {
        val instant = Instant.ofEpochSecond(dt)
        val date = Date.from(instant)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.ENGLISH)
        return formatter.format(date.toInstant().atZone(ZoneId.systemDefault()))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedTime(dt:Long): String {
        val instant = Instant.ofEpochSecond(dt)
        val time = LocalTime.from(instant.atZone(ZoneId.systemDefault()))
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return formatter.format(time)
    }
    fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        var geocoder = Geocoder(applicationContext, Locale.getDefault())
        if(sharedPreferences.getString("language", "en").toString().equals("ar")) {
            geocoder = Geocoder(applicationContext, Locale("ar")) // Specify Arabic locale
        }
        var country: String? = null
        var city: String? = ""
        var city2: String? = ""
        var city3: String? = ""


        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    Log.d("full adressList" , addresses.toString())
                    val address = addresses[0]
                    country = address.countryName

                    city = if(address.adminArea.isBlank()){
                        ""
                    } else{
                        address.adminArea.plus(" , ")

                    }
                    city2 = if(address.locality.isBlank()){
                        ""
                    } else{
                        address.locality.plus(" , ")

                    }


                    city3 =address.subLocality
                    Log.d("adress",country+"-"+city+"-"+city2+"-"+city3+"-")

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return city2+""+city+""+country

    }


    fun getCountryFromLatLng(latitude: Double, longitude: Double): String? {
        var geocoder = Geocoder(applicationContext, Locale.getDefault())
        if(sharedPreferences.getString("language", "en").toString().equals("ar")) {
            geocoder = Geocoder(applicationContext, Locale("ar")) // Specify Arabic locale
        }
        var country: String? = null
        var city: String? = ""
        var city2: String? = ""
        var city3: String? = ""


        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    Log.d("full adressList" , addresses.toString())
                    val address = addresses[0]

                    country = if(address.countryName.isBlank()){
                        address.adminArea
                    } else{

                        address.countryName
                    }



                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return country

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun isDayTime(timestamp: Long, sunriseTimestamp: Long, sunsetTimestamp: Long): Boolean {
        val currentTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC)
        val sunriseTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sunriseTimestamp), ZoneOffset.UTC)
        val sunsetTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sunsetTimestamp), ZoneOffset.UTC)

        return currentTime.isAfter(sunriseTime) && currentTime.isBefore(sunsetTime)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedTime(dt: Long, timezone: String): String {
        val instant = Instant.ofEpochSecond(dt)
        val time = LocalTime.from(instant.atZone(ZoneId.of(timezone)))
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return formatter.format(time)
    }

}