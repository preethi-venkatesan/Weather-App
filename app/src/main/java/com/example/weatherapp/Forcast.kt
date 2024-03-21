package com.example.weatherapp

data class Forcast(
    val city: City?,
    val cnt: Int,
    val cod: String,
    val list: ArrayList<WeatherList> = arrayListOf(),
    val message: Int
)