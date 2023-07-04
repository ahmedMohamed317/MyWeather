package com.example.myweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeViewModleFactory(private val iRepo: RepoInterface,
                           private val lat: String,
                           private val long: String ,
                           private val units :String ,
                           private val lang :String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(iRepo , lat, long , units , lang) as T
        }
        throw IllegalArgumentException("Class Not found")
    }
}