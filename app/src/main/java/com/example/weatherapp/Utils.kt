package com.example.weatherapp

class Utils {
    companion object{

        var BASE_URL: String= "https://api.openweathermap.org/data/2.5/"
        var API_KEY : String= "994a33f56f0e8f7669747db72553310a"
        const val PERMISSION_REQUEST_CODE = 123

        var WEATHER_CLEAR_SKY : Int = 800
        var WEATHER_LIGHT_RAIN : Int = 500
        var WEATHER_FEW_CLOUDS : Int = 801
        var WEATHER_SCATTERED_CLOUDS : Int = 802
        var WEATHER_BROKEN_CLOUDS : Int = 803

    }
}