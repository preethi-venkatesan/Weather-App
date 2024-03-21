package com.example.weatherapp

data class City(
    val coord: Coord,
    val country: String,
    val id: Int,
    val name: String,
    val population: Int,
    var sunrise: Int,
    var sunset: Int,
    val timezone: Int
)