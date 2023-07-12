package com.example.myweather.network

import android.util.Log
import com.example.myweather.model.City
import com.example.myweather.model.WeatherResponse
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
                units ="metric" ,
                lang =lang ,
                apiKey = "68a39f5322e1e6f73d05d56cad1c1dd5"
                    )
             response.body().let { Log.d("ApiClient", it?.timezone ?: "it's null") }
             Log.d("ApiClient", response.body()!!.lat.toString())
             Log.d("ApiClient", response.body()!!.timezone_offset.toString())
             return response




        }




    override suspend fun getAutoComplete(city: String): Response<List<City>> {
        Log.d("ApiClient", city)

        val response = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/")
            .build()
            .create(ApiService::class.java).getAutoComplete(city,"68a39f5322e1e6f73d05d56cad1c1dd5")
        response.body()?.get(0).let { Log.d("ApiClient", it?.country ?: "it's null") }
        Log.d("ApiClient", response.body()!!.get(0).lat.toString())
        return response




    }

}



