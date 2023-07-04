package com.example.myweather

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.myweather.databinding.FragmentHomeBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
//    private lateinit var latitude :String
//    private lateinit var longitude :String
    // private lateinit var localSource: LocalSource
    private lateinit var homeViewModel :HomeViewModel
    private var language :String = "en"
    private var unit :String = "metric"
    private lateinit var latitude :String
    private lateinit var longitude :String
    var remoteSource : RemoteSource = ApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          latitude = arguments?.getString("latitude").toString()
          longitude = arguments?.getString("longitude").toString()
        latitude = "22"
        longitude = "11"
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
         val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        language = sharedPreferences?.getString("language", "en").toString()
        unit = sharedPreferences?.getString("units", "metric").toString()
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




        val homeViewModelFactory = HomeViewModleFactory(
            Repo.getInstance(remoteSource,ConcreteLocalSource.getInstance(requireContext())), latitude, longitude, unit, language
        )

//        ViewModelProvider(requireActivity()).viewModelStore.clear()
        //requireActivity().viewModelStore.clear()
        homeViewModel = ViewModelProvider(requireActivity(), homeViewModelFactory).get(HomeViewModel::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.getAndEmitData(latitude,longitude,unit,language)
            homeViewModel.weatherResponseFlow.collectLatest { status ->
                Log.d("insideflow", "onView: ${status.javaClass}")
                when (status) {
                    is ApiState.Loading -> {
                        Log.d("insideLoading", "onView: ${status.javaClass}")

                    }
                    is ApiState.Success -> {

                        Log.d(TAG, "status: Success")
                        dailyAdapter.submitList(status.weatherResponse?.daily)
                        hourlyAdapter.submitList(status.weatherResponse?.hourly)
                        status.weatherResponse?.let { setDataToViews(it) }
                        Log.d(TAG, status.weatherResponse!!.timezone.toString())


                    }
                    else -> {
                        Log.d("insideElse", "onView: ${status.javaClass}")

                    }
                }
            }
        }
                    return root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun setDataToViews(weatherResponse: WeatherResponse) {

        binding.sunriseTv.text = getFormattedTime(weatherResponse.current.sunrise)
        binding.sunsetTv.text = getFormattedTime(weatherResponse.current.sunset)
        binding.longTv.text = weatherResponse.lon.toString()
        binding.latTv.text = weatherResponse.lat.toString()
        binding.cloudTv.text = weatherResponse.current.clouds.toString()
        binding.pressureTv.text = weatherResponse.current.pressure.toString()
        binding.uvTv.text = weatherResponse.current.uvi.toString()
        binding.rainTv.text = weatherResponse.current.humidity.toString()
        binding.visibilityTv.text = weatherResponse.current.visibility.toString()
        binding.windtTv.text = weatherResponse.current.wind_speed.toString()
        binding.txtViewLocation.text ="TimeZone : " + weatherResponse.timezone
        binding.txtViewAddrress.text = getAddressFromLatLng(weatherResponse.lat,weatherResponse.lon)
        binding.textViewDate.text =getFormattedDate( weatherResponse.current.dt)
        binding.txtViewTemperatureDegree.text = weatherResponse.current.temp.toString().plus("°")
        val iconString = weatherResponse.current.weather[0].icon
        val urlString = "https://openweathermap.org/img/wn/$iconString@4x.png"
        Glide.with(requireContext())
            .load(urlString)
            .into(binding.imageViewWeatherIcon)
        binding.txtViewWeatherCondition.text = weatherResponse.current.weather[0].main
        binding.descriptionTv.text = weatherResponse.current.weather[0].description
        binding.textViewhour.text = getFormattedTime(weatherResponse.current.dt)





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
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var country: String? = null
        var city: String? = ""
        var city2: String? = ""
        var city3: String? = ""


        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    country = address.countryName
                    city = address.adminArea
                    city2 =address.locality
                    city3 =address.subLocality
                    Log.d("adress",country+"-"+city+"-"+city2+"-"+city3+"-")

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return city2+" , "+city+" , "+country

    }







}
