package com.example.myweather.ui.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.example.myweather.R
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedDate(dt:Long): String {
    val instant = Instant.ofEpochSecond(dt)
    val date = Date.from(instant)
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.ENGLISH)
    return formatter.format(date.toInstant().atZone(ZoneId.systemDefault()))
}
@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedDateWithoutYear(dt: Long): String {
    val instant = Instant.ofEpochSecond(dt)
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
    val formatter = DateTimeFormatter.ofPattern("dd MMMM").withLocale(Locale.ENGLISH)
    return formatter.format(zonedDateTime)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedTime(dt: Long): String {
    val instant = Instant.ofEpochSecond(dt)
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    return formatter.format(zonedDateTime)
}

fun getAddress(context: Context, long: Double, lat:Double, locale: Locale, onResult: (Address?) -> Unit) {
    var address: Address?
    val geocoder = Geocoder(context, locale)


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(
            lat, long, 1
        ) {
            address = it[0]
            onResult(address)
        }
    } else {
        val addressList = geocoder.getFromLocation(lat, long, 1)
        if(!addressList.isNullOrEmpty()){
            address = geocoder.getFromLocation(lat, long, 1)?.get(0)
            onResult(address)
        }else{
            onResult(null)
        }

    }
}

fun changeLanguageLocaleTo(context: Context, language: String) {
    val locale = Locale(language)
    val configuration = context.resources.configuration
    configuration.setLocale(locale)
    context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
}
fun getLanguageLocale(): String {
    return AppCompatDelegate.getApplicationLocales().toLanguageTags()
}
fun changeTimeZoneToTime(timeZoneString: String): String {
    val timeZone = TimeZone.getTimeZone(timeZoneString)
    val currentTime = System.currentTimeMillis()
    val calendar = Calendar.getInstance(timeZone)
    calendar.timeInMillis = currentTime
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val amPm = if (hour < 12) "AM" else "PM"
    val formattedHour = if (hour > 12) hour - 12 else hour
    val formattedTime = String.format("%02d:%02d %s", formattedHour, minute, amPm)
    return formattedTime
}
fun changeTimeZoneToTimestamp(timeZoneString: String): Long {
    val timeZone = TimeZone.getTimeZone(timeZoneString)
    val calendar = Calendar.getInstance(timeZone)
    val currentTime = System.currentTimeMillis()
    calendar.timeInMillis = currentTime
    return calendar.timeInMillis / 1000
}

fun TextView.setWindSpeed(windSpeed: Double , sharedPreferences: SharedPreferences) {
    when (sharedPreferences.getString("measurement","M/S")) {
        "M/S"-> text =
            buildString {
                append(windSpeed.roundToInt().toString())
                append(" m/s")
            }
        "Km/Hr" -> text = buildString {
            append(getWindSpeedInMilesPerHour(windSpeed).roundToInt().toString())
            append(" km/hr")
        }
    }
}

fun getWindSpeedInMilesPerHour(windSpeed: Double): Double {
    return windSpeed * 2.23694
}

fun TextView.setTemp(temp: Int, sharedPreferences: SharedPreferences) {
    val symbol: String
    val theTemp = when (sharedPreferences.getString("units","Celsius")) {
        "celsius","سيليزيوس" -> {
            symbol = "°C"
            temp
        }
        "fahrenheit","فهرنهايت" -> {
            symbol = "°F"
            (((temp) * 9.0 / 5) + 32).roundToInt()
        }
        "kelvin","كيلفن" -> {
            symbol = "°K"
            (temp) + 273
        }
        else -> {
            symbol = "°C"
            temp
        }
    }
    text = buildString {
        append(theTemp)
        append(" ")
        append(symbol)
    }
}

