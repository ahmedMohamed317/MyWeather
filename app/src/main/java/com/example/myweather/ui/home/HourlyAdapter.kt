package com.example.myweather.ui.home

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.databinding.HourlyItemBinding
import com.example.myweather.ui.settings.getFormattedTime
import com.example.myweather.model.HourlyWeather
import com.example.myweather.ui.settings.setTemp
import java.util.*

class HourlyAdapter( var context: Context) : ListAdapter<HourlyWeather, HourlyAdapter.ViewHolder>(
    DiffUtils
) {
    private val cardColors = arrayOf(
        R.color.card1,
        R.color.card2,
        R.color.card3,
        R.color.card4,
        R.color.card5,
        R.color.card6,
        R.color.card7

        )
    val sharedPreferences =context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
    class ViewHolder(val binding: HourlyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HourlyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val daily = getItem(position)
        val cardColor = cardColors[position % cardColors.size]
        val context = holder.itemView.context
        holder.binding.cardviewhourly.setBackgroundColor(ContextCompat.getColor(context, cardColor))
        if (position == 0) {
            holder.binding.textViewTime.text = context.getString(R.string.now)
        } else {
            holder.binding.textViewTime.text = getFormattedTime(daily.dt)
        }
        val iconString = daily.weather[0].icon
        val urlString = "https://openweathermap.org/img/wn/$iconString.png"
        Glide.with(this.context)
            .load(urlString)
            .into(holder.binding.imageViewWeatherIcon)


        holder.binding.textViewTempDegreeAvg.setTemp(daily.temp.toInt(),sharedPreferences)
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