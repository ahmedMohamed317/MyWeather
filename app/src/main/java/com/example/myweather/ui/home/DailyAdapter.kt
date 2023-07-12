package com.example.myweather.ui.home

import android.annotation.SuppressLint
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
import com.example.myweather.databinding.DailyItemBinding
import com.example.myweather.ui.settings.getFormattedDateWithoutYear
import com.example.myweather.model.DailyWeather
import com.example.myweather.ui.settings.setTemp
import java.util.*

class DailyAdapter(var context: Context) : ListAdapter<DailyWeather, DailyAdapter.ViewHolder>(
    DiffUtils
) {
    private val cardColors = arrayOf(
        R.color.card7,
        R.color.card6,
        R.color.card5,
        R.color.card4,
        R.color.card3,
        R.color.card2,
        R.color.card1

    )
    val sharedPreferences =context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
    class ViewHolder(val binding: DailyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DailyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val daily = getItem(position)
        val cardColor = cardColors[position % cardColors.size]
        val context = holder.itemView.context
        holder.binding.cardviewdaily.setBackgroundColor(ContextCompat.getColor(context, cardColor))


        if (position == 0) {
            holder.binding.textViewDay.text = context.getString(R.string.today)

        }
        else if (position == 1) {
            holder.binding.textViewDay.text = context.getString(R.string.tomorrow)}

        else {
            holder.binding.textViewDay.text = getFormattedDateWithoutYear(daily.dt)
        }
        val iconString = daily.weather[0].icon
        val urlString = "https://openweathermap.org/img/wn/$iconString.png"
        Glide.with(this.context)
            .load(urlString)
            .into(holder.binding.imageViewWeatherIcon)

        holder.binding.textViewTempDegreeTop.setTemp(daily.temp.max.toInt(),sharedPreferences)
        holder.binding.textViewTempDegreeLeast.setTemp(daily.temp.min.toInt(),sharedPreferences)
        holder.binding.textViewTempDegreeAvg.setTemp(daily.temp.day.toInt(),sharedPreferences)

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