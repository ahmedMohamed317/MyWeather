package com.example.myweather

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedDate(dt:Long): String {
    val instant = Instant.ofEpochSecond(dt)
    val date = Date.from(instant)
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.ENGLISH)
    return formatter.format(date.toInstant().atZone(ZoneId.systemDefault()))
}

@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedDateWithoutYear(dt:Long): String {
    val instant = Instant.ofEpochSecond(dt)
    val date = Date.from(instant)
    val formatter = DateTimeFormatter.ofPattern("dd MMMM").withLocale(Locale.ENGLISH)
    return formatter.format(date.toInstant().atZone(ZoneId.systemDefault()))
}

@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedTime(dt:Long): String {
    val instant = Instant.ofEpochSecond(dt)
    val time = LocalTime.from(instant.atZone(ZoneId.systemDefault()))
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    return formatter.format(time)
}

