package com.example.myweather.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.database.RepoInterface


class SearchViewModelFactory(private val iRepo: RepoInterface,
                         ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(iRepo ) as T
        }
        throw IllegalArgumentException("Class Not found")
    }
}