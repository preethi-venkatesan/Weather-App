package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather?")
    fun getCurrentWeather(
        @Query("lat")
        lat: Double,
        @Query("lon")
        lon: Double,
        @Query("appid")
        appid: String = Utils.API_KEY

    ): Call<CurrentWeather>

    @GET("forecast?")
    fun getWeatherByCity(
        @Query("q")
        city: String,

        @Query("appid")
        appid: String = Utils.API_KEY

    ): Call<Forcast>

}