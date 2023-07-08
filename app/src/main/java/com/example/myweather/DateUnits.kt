package com.example.myweather

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.text.SimpleDateFormat
import java.util.*

fun getTimeFormat(timeInMilliSecond: Long): String {
    val date = Date(timeInMilliSecond)
    val convertFormat =
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    return convertFormat.format(date).toString()
}

fun getCurrentDateFormat(): String {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale(getLanguageLocale()))
    return simpleDateFormat.format(Date(System.currentTimeMillis()))
}


fun getACompleteDateFormat(timeInMilliSecond: Long,timeZone : TimeZone):String{
    val pattern = "dd MMMM - yyyy"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale(getLanguageLocale()))
    simpleDateFormat.timeZone = timeZone
    return simpleDateFormat.format(Date(timeInMilliSecond))
}
fun getADateFormat(timeInMilliSecond: Long):String{
    val pattern = "dd MMMM"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale(getLanguageLocale()))
    return simpleDateFormat.format(Date(timeInMilliSecond))
}
