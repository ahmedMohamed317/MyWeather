package com.example.myweather.ui.home

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.apistates.ApiState
import com.example.myweather.database.ConcreteLocalSource
import com.example.myweather.database.Repo
import com.example.myweather.databinding.FragmentHomeBinding
import com.example.myweather.model.WeatherResponse
import com.example.myweather.network.ApiClient
import com.example.myweather.network.RemoteSource
import com.example.myweather.network.isInternetConnected
import com.example.myweather.ui.settings.changeTimeZoneToTime
import com.example.myweather.ui.settings.changeTimeZoneToTimestamp
import com.example.myweather.ui.settings.setTemp
import com.example.myweather.ui.settings.setWindSpeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var homeViewModel : HomeViewModel
    private var language :String = "en"
    private var unit :String = "metric"
    private var measurement :String = "M/S"
    private lateinit var latitude :String
    private lateinit var longitude :String
    var remoteSource : RemoteSource = ApiClient()
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        latitude = arguments?.getString("latitude").toString()?: ""
        longitude = arguments?.getString("longitude").toString()?: ""
        sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        Log.d("on oncreate home",latitude)
        if(latitude!=""&&latitude!="null"){
            editor.putString("latitude",latitude )
            editor.putString("longitude",longitude )
            editor.apply()
            Log.d("on sharedprf if home",latitude)

        }
        else{
            latitude=sharedPreferences.getString("latitude", "0").toString()
            longitude=sharedPreferences.getString("longitude", "0").toString()
            Log.d("on sharedprf else home",latitude)


        }
        Log.d("on oncreate",language)
        Log.d("on oncreate home",longitude)



    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dailyAdapter = DailyAdapter(requireContext())
        hourlyAdapter = HourlyAdapter(requireContext())
        binding.recyclerViewHours.adapter = hourlyAdapter
        binding.recyclerViewDays.adapter = dailyAdapter


        language = sharedPreferences.getString("language", "en").toString()
        unit = sharedPreferences.getString("units", "metric").toString()
        measurement = sharedPreferences.getString("measurement", "M/S").toString()

        Log.d("on oncreateview",language)
        Log.d("on oncreateview",unit)

//        val repo = HomeViewModleFactory (
//            Repo.getInstance(ApiClient), latitude , longitude , unit , language
//        )
//        requireActivity().viewModelStore.clear()
//        val homeViewModelLazy : HomeViewModel by lazy {
//
//            ViewModelProvider(requireActivity(),repo )[HomeViewModel::class.java]
//        }
//        homeViewModel = homeViewModelLazy



        val localSource =ConcreteLocalSource.getInstance(requireContext())
        val homeViewModelFactory = HomeViewModleFactory(
            Repo.getInstance(remoteSource, localSource), latitude, longitude, unit, language
        )
//        ViewModelProvider(requireActivity()).viewModelStore.clear()
        //requireActivity().viewModelStore.clear()
        homeViewModel = ViewModelProvider(requireActivity(), homeViewModelFactory).get(HomeViewModel::class.java)
        onLoadingState()

        viewLifecycleOwner.lifecycleScope.launch {
            if (isInternetConnected(requireContext())) {
                homeViewModel.getAndEmitData(latitude, longitude, unit, language)
                homeViewModel.weatherResponseFlow.collectLatest { status ->
                    Log.d("insideflow", "onView: ${status.javaClass}")
                    when (status) {
                        is ApiState.Loading -> {
                            launch(Dispatchers.Main) {
                                Log.d("insideLoading", "onView: ${status.javaClass}")
                                onLoadingState()
                            }

                        }
                        is ApiState.Success -> {
//                            onSuccessState()
                            launch(Dispatchers.Main) {
                                onSuccessState()
                            }
                            Log.d(TAG, "status: Success")
                            dailyAdapter.submitList(status.weatherResponse?.daily)
                            hourlyAdapter.submitList(status.weatherResponse?.hourly)
                            status.weatherResponse?.let { setDataToViews(it) }
                            val weatherResponse = status.weatherResponse
                            if (weatherResponse != null) {
                                weatherResponse.id="noInternetId"
                                weatherResponse.minutely = emptyList()
                                Log.d(TAG, weatherResponse.id)

                            }
                            if (weatherResponse != null) {
                                try {
                                       launch(Dispatchers.IO){

                                    ConcreteLocalSource.getInstance(requireContext())
                                        .insertIntoFav(weatherResponse)}
                                }
                                catch (e : Exception){
                                    Log.d("catch on save ", e.message.toString())
                                }
                            }
                            Log.d(TAG, status.weatherResponse!!.timezone.toString())



                        }
                        else -> {
                            Log.d("insideElse", "onView: ${status.javaClass}")

                        }
                    }
                }
            }
            else {
                    var weatherPojo: WeatherResponse? =null
                onSuccessState()
                launch(Dispatchers.IO) {
                     weatherPojo = localSource.getFavWithId("noInternetId")
                    dailyAdapter.submitList(weatherPojo!!.daily)
                    hourlyAdapter.submitList(weatherPojo!!.hourly)
                    Log.d("weatherpojo", weatherPojo!!.lon.toString())
                }.join()
                launch ( Dispatchers.Main ){
                weatherPojo?.let { setDataToViews(it) }
                    Toast.makeText(requireContext(),requireContext().getString(R.string.home_toast),Toast.LENGTH_LONG).show()
                }
                }


            }

                    return root
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


        if (weatherResponse.timezone!="Africa/Cairo")
        {
            binding.sunriseTv.text = getFormattedTime(weatherResponse.current.sunrise,weatherResponse.timezone)
            binding.sunsetTv.text = getFormattedTime(weatherResponse.current.sunset,weatherResponse.timezone)
        }
        else{

            binding.sunriseTv.text = getFormattedTime(weatherResponse.current.sunrise)
            binding.sunsetTv.text = getFormattedTime(weatherResponse.current.sunset)
        }
            binding.textViewhour.text =getFormattedTime( weatherResponse.current.dt)
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
        Glide.with(requireContext())
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedTime(dt: Long, timezone: String): String {
        val instant = Instant.ofEpochSecond(dt)
        val time = LocalTime.from(instant.atZone(ZoneId.of(timezone)))
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return formatter.format(time)
    }

    fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        var geocoder = Geocoder(requireContext(), Locale.getDefault())
        if(language.equals("ar")) {
             geocoder = Geocoder(requireContext(), Locale("ar")) // Specify Arabic locale
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
        var geocoder = Geocoder(requireContext(), Locale.getDefault())
        if(language.equals("ar")) {
            geocoder = Geocoder(requireContext(), Locale("ar")) // Specify Arabic locale
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

     fun onLoadingState (){

        binding.cardView1.isVisible = false
        binding.cardView2.isVisible = false
        binding.progressBar.isVisible = true
        binding.textViewhour.isVisible = false
        binding.textViewDate.isVisible = false
        binding.sunsetTv.isVisible = false
        binding.sunriseTv.isVisible = false
        binding.Sunrise.isVisible = false
        binding.Sunset.isVisible = false
        binding.txtViewLocation.isVisible = false
        binding.txtViewTemperatureDegree.isVisible = false
        binding.txtViewWeatherCondition.isVisible = false
        binding.descriptionTv.isVisible = false
        binding.imageView10.isVisible = false
        binding.imageViewWeatherIcon.isVisible = false
        binding.imageView11.isVisible = false

        binding.textView22.isVisible = false
        binding.textView23.isVisible = false
        binding.recyclerViewDays.isVisible = false
        binding.recyclerViewHours.isVisible = false
        binding.txtViewAddrress.isVisible = false


    }
     fun onSuccessState (){

        binding.cardView1.isVisible = true
        binding.cardView2.isVisible = true
        binding.progressBar.isVisible = false
        binding.textViewhour.isVisible = true
        binding.textViewDate.isVisible = true
        binding.sunsetTv.isVisible = true
        binding.sunriseTv.isVisible = true
        binding.Sunrise.isVisible = true
        binding.Sunset.isVisible = true
        binding.txtViewLocation.isVisible = true
        binding.txtViewTemperatureDegree.isVisible = true
        binding.txtViewWeatherCondition.isVisible = true
        binding.descriptionTv.isVisible = true
        binding.imageView10.isVisible = true
        binding.imageViewWeatherIcon.isVisible = true
        binding.imageView11.isVisible = true

        binding.textView22.isVisible = true
        binding.textView23.isVisible = true
        binding.recyclerViewDays.isVisible = true
        binding.recyclerViewHours.isVisible = true
        binding.txtViewAddrress.isVisible = true


    }



}
