package com.example.myweather.ui.settings

import android.widget.TextView
import com.example.myweather.getADateFormat
import com.example.myweather.getTimeFormat


fun TextView.setTime(timeInMilliSecond: Long) {
    text = getTimeFormat(timeInMilliSecond)
}

fun TextView.setDate(timeInMilliSecond: Long){
    text = getADateFormat(timeInMilliSecond)
}

