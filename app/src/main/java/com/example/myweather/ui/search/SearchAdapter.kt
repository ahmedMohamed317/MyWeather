package com.example.myweather.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.databinding.DailyItemBinding
import com.example.myweather.databinding.SearchItemBinding
import com.example.myweather.model.City
import com.example.myweather.model.DailyWeather
import com.example.myweather.model.WeatherResponse
import com.example.myweather.network.ApiClient
import com.example.myweather.ui.favorite.FavoriteDetailsActivity
import com.example.myweather.ui.home.DailyAdapter
import com.example.myweather.ui.settings.getFormattedDateWithoutYear
import com.example.myweather.ui.settings.setTemp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchAdapter (var context: Context) : ListAdapter<City, SearchAdapter.ViewHolder>(
    DiffUtils
) {

    class ViewHolder(val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = getItem(position)
        holder.binding.cityName.text = city.name
        holder.binding.country.text = city.country
        holder.binding.state.text = city.state
        holder.binding.root.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = ApiClient().getWeather(city.lat.toString(), city.lon.toString(), "metric", "en")
                    if (response.isSuccessful) {
                        val weatherData : WeatherResponse? = response.body()
                        val intent = Intent(context, FavoriteDetailsActivity::class.java)
                        intent.putExtra("EXTRA_OBJECT", weatherData)
                        context.startActivity(intent)


                    } else {
                        Log.d("at searchAdapter","else")
                        }
                } catch (e: Exception) {
                    Log.d("at searchAdapter",e.message.toString())

                }
            }
        }




    }
    object DiffUtils : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }

    }
}