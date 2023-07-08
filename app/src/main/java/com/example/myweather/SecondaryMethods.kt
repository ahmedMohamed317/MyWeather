package com.example.myweather

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
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