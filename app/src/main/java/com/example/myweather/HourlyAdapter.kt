package com.example.myweather

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.databinding.DailyItemBinding
import com.example.myweather.databinding.HourlyItemBinding
import java.util.*

class HourlyAdapter( var context: Context) : ListAdapter<HourlyWeather, HourlyAdapter.ViewHolder>(DiffUtils) {
    class ViewHolder(val binding: HourlyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HourlyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val daily = getItem(position)

        if (position == 0) {
            holder.binding.textViewTime.text = "Now"
        } else {
            holder.binding.textViewTime.text = getFormattedTime(daily.dt)
        }
        val iconString = daily.weather[0].icon
        val urlString = "https://openweathermap.org/img/wn/$iconString.png"
        Glide.with(this.context)
            .load(urlString)
            .into(holder.binding.imageViewWeatherIcon)


        holder.binding.textViewTempDegreeAvg.text = daily.temp.toString().plus("°")
    }
    object DiffUtils : DiffUtil.ItemCallback<HourlyWeather>() {
        override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem == newItem
        }

    }
}