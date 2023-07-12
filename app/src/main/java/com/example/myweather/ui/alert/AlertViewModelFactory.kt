package com.example.myweather.ui.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.database.RepoInterface

class AlertViewModelFactory(private val iRepo: RepoInterface): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            return AlertViewModel(iRepo) as T
        }
        throw IllegalArgumentException("Class not found")
    }
}