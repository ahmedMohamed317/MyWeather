package com.example.myweather

import android.util.Log
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class  ApiClient : RemoteSource {

//
//    val retrofit: ApiService by lazy {
//        Retrofit.Builder()
//            .addConverterFactory(GsonConverterFactory.create())
//            .baseUrl("https://api.openweathermap.org/")
//            .build()
//            .create(ApiService::class.java)
//    }

     override suspend fun getWeather(lat: String, lon: String , units : String , lang:String): Response<WeatherResponse> {
             Log.d("ApiClient", "before getweather method"+units+lang)

            val response = Retrofit.Builder()
                 .addConverterFactory(GsonConverterFactory.create())
                 .baseUrl("https://api.openweathermap.org/")
                 .build()
                 .create(ApiService::class.java).getWeather(
                lat = lat,
                lon = lon ,
                units =units ,
                lang =lang ,
                apiKey = "68a39f5322e1e6f73d05d56cad1c1dd5"
                    )
             response.body().let { Log.d("ApiClient", it?.timezone ?: "it's null") }
             Log.d("ApiClient", response.body()!!.lat.toString())
             Log.d("ApiClient", response.body()!!.timezone_offset.toString())
             return response




        }
}



