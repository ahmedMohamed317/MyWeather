package com.example.myweather

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.databinding.DailyItemBinding
import java.util.*

class DailyAdapter(var context: Context) : ListAdapter<DailyWeather, DailyAdapter.ViewHolder>(DiffUtils) {
    class ViewHolder(val binding: DailyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DailyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val daily = getItem(position)

        if (position == 0) {
            holder.binding.textViewDay.text = "Today"

        }
        else if (position == 1) {
            holder.binding.textViewDay.text = "Tomorrow"}

        else {
            holder.binding.textViewDay.text = getFormattedDateWithoutYear(daily.dt)
        }
        val iconString = daily.weather[0].icon
        val urlString = "https://openweathermap.org/img/wn/$iconString.png"
        Glide.with(this.context)
            .load(urlString)
            .into(holder.binding.imageViewWeatherIcon)

        holder.binding.textViewTempDegreeAvg.text = daily.temp.day.toString().plus("°")
        holder.binding.textViewTempDegreeTop.text = daily.temp.max.toString().plus("°")
        holder.binding.textViewTempDegreeLeast.text = daily.temp.min.toString().plus("°")

    }
    object DiffUtils : DiffUtil.ItemCallback<DailyWeather>() {
        override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem == newItem
        }

    }
}