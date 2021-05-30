package com.app.tempatmyplace.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.tempatmyplace.model.CityWeather
import com.app.tempatmyplace.network.WeatherApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object WeatherRepository {
    val cityWeather = MutableLiveData<CityWeather?>()

    fun getWeatherAPICall(lat: Double, lon: Double, unit: String, language: String): MutableLiveData<CityWeather?> {
        CoroutineScope(IO).launch {
            val weatherData = WeatherApi.retrofitService.getCityWeatherData(
                lat,
                lon,
                "e70b7dc642de665d318837ffb9a0baf3",
                unit,
                language,
                "minutely,alerts"
            )

            weatherData.enqueue(object : Callback<CityWeather> {
                override fun onResponse(
                    call: Call<CityWeather>,
                    response: Response<CityWeather>
                ) {
                    val data = response.body()
                    cityWeather.value = data
                }

                override fun onFailure(call: Call<CityWeather>, t: Throwable) {
                    Log.d("WeatherRepository", "onFailure:" + t.message)
                }
            })
        }
        return cityWeather
    }
}