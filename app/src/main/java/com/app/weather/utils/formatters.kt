package com.app.weather.utils

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import androidx.core.net.toUri
import com.app.weather.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.*

fun dateFormat(pattern: String, date: Long): String {
    return SimpleDateFormat(pattern, Locale.ENGLISH).format( date * 1000)
}

fun dateFormatDate(date: Long): String {
    return dateFormat("E dd/MM/yyyy hh:mm a", date)
}

fun dateFormatDay(date: Long): String {
    return dateFormat("E dd/MM", date)
}

fun dateFormatHour(date: Long): String {
    return dateFormat("hh:mm", date)
}

fun setImage(imageStatus: ImageView, status: String) {
    val img = "openweathermap.org/img/wn/$status.png"
    val imgUri = img.toUri().buildUpon().scheme("http").build()
    Glide.with(imageStatus.context)
            .load(imgUri)
            .apply(
                    RequestOptions()
                            .placeholder(R.drawable.ic_sunrise)
                            .error(R.drawable.ic_smile)
            )
            .into(imageStatus)
}

fun saveSettings(activity: Activity, temp: String) {
    //read from xml
    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
    with (sharedPref.edit()) {
        putString("units", temp)
        apply()
    }
}

fun getSettings(activity: Activity): String? {
    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return null
    val defaultValue = "metric"
    return sharedPref.getString("units", defaultValue)
}