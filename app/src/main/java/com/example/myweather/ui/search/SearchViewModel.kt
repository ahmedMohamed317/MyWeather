package com.example.myweather.ui.search

import androidx.lifecycle.ViewModel
import com.example.myweather.apistates.ApiState
import com.example.myweather.apistates.AutoCompleteApiState
import com.example.myweather.database.RepoInterface
import kotlinx.coroutines.flow.MutableStateFlow

class SearchViewModel(private val iRepo: RepoInterface) : ViewModel(){
    val cityFlow : MutableStateFlow<AutoCompleteApiState> = MutableStateFlow(AutoCompleteApiState.Loading)



    suspend fun getAutoComplete(city: String)
    {
        try {
//            Log.d("insidegetandemit", "inside try")

            val city = iRepo.getAutoComplete(city)
            cityFlow.emit(  AutoCompleteApiState.Success(city))
        } catch (e: Exception) {
//            Log.d("insidegetandemitcattch", e.message.toString())
            cityFlow.emit(AutoCompleteApiState.Failure(e.message.toString()))
        }
    }
}