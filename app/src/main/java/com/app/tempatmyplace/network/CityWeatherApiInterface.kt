package com.app.tempatmyplace.network

import com.app.tempatmyplace.model.CityWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//interface ApiInterface {
//    @GET("data/2.5/weather?")
//    fun getCityData(
//        @Query("q") q: String,
//        @Query("appid") appId: String,
//        @Query("units") units: String,
//        @Query("lang") language: String
//    ): Call<City>
//}

interface CityWeatherApiInterface {
    @GET("data/2.5/onecall?")
    fun getCityWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appId: String,
        @Query("units") units: String,
        @Query("lang") language: String,
        @Query("exclude") exclude: String
    ): Call<CityWeather>
}