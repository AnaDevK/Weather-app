package com.app.tempatmyplace.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val BASE_URL = "http://api.openweathermap.org/"

val retrofitBuilder = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object WeatherApi {
    val retrofitService: CityWeatherApiInterface by lazy {
        retrofitBuilder.create(
            CityWeatherApiInterface::class.java
        )
    }
}