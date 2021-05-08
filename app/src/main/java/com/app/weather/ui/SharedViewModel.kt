package com.app.weather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.weather.model.CityWeather
import com.app.weather.model.Daily
import com.app.weather.repository.WeatherRepository

class SharedViewModel : ViewModel() {
    private var currentWeatherLiveData: MutableLiveData<CityWeather?>? = null
    val weeklyLiveData = MutableLiveData<List<Daily>>()
    var units: MutableLiveData<String>? = null



    fun getWeather(lat: Double, lon: Double, unit: String, language: String): LiveData<CityWeather?>? {
        currentWeatherLiveData = WeatherRepository.getWeatherAPICall(lat, lon, unit, language)
        return currentWeatherLiveData
    }

    fun setWeeklyWeather(days: List<Daily>) {
        weeklyLiveData.value = days
    }
}