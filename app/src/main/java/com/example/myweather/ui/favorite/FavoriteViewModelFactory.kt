package com.example.myweather.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.database.RepoInterface

class FavoriteViewModelFactory (private val iRepo: RepoInterface): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(iRepo) as T
        }
        throw IllegalArgumentException("Class not found")
    }
}