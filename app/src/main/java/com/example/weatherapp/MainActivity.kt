package com.example.weatherapp

import android.content.ContentValues
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.Manifest
import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.searchView.setOnClickListener {
            val city = binding.etCityName.text.toString()
            if (city.isNotEmpty()) {
                callWeatherApi(city)
                binding.etCityName.setText("")
            } else {
                Toast.makeText(this, "Enter the city name", Toast.LENGTH_SHORT).show()
            }
        }
        setContentView(binding.root)

        // calling api for the first time based on current location


        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocation()
        }
    }


    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations, this can be null.
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    callCurrentWeatherApi(latitude, longitude, Utils.API_KEY)
                } else {
                    // Location is null, handle accordingly
                    Toast.makeText(
                        this,
                        "Unable to retrieve current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                Toast.makeText(
                    this,
                    "Failed to get location: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun callCurrentWeatherApi(latitude: Double, longitude: Double, apiKey: String) {
        val retrofitBuilder = Retrofit.Builder().baseUrl(Utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val retrofitData = retrofitBuilder.getCurrentWeather(latitude, longitude, apiKey)

        retrofitData.enqueue(object : Callback<CurrentWeather?> {
            override fun onResponse(
                call: Call<CurrentWeather?>,
                response: Response<CurrentWeather?>
            ) {
                val responseBody = response.body()

                if (responseBody != null) {

                    handleCurrentWeatherUI(response.body()!!)
                } else {
                    Toast.makeText(this@MainActivity, "Please try again later", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<CurrentWeather?>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Please try again later", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun handleCurrentWeatherUI(body: CurrentWeather) {

        // calling forecast API
        callWeatherApi(body.name)

        val currentDate = LocalDate.now()
        val currentDay = currentDate.format(DateTimeFormatter.ofPattern("EEEE"))
        binding.day.text = currentDay

        var weatherItem : Weather = body.weather[0]
        binding.WeatherForecast.text = weatherItem.main
        binding.areaName.text = body.name
        binding.HumidityValue.text = body.main.humidity.toString()
        binding.WindSpeedValue.text = body.wind.speed.toString()
        binding.PressureValue.text = body.main.pressure.toString()


        var sunriseTimeString: String = epochToDateTime(body.sys.sunrise)
        binding.SunriseValue.text = sunriseTimeString

        var sunsetTime: String = epochToDateTime(body.sys.sunset)
        binding.SunsetValue.text = sunsetTime

        var tempInKelvin: Double = body.main.temp
        var celsius: Double = kelvinToFahrenheit(tempInKelvin)
        binding.degreeCelsi.text = "$celsius°C"


    }

    private fun epochToDateTime(epoch: Long): String {
        val dateTime = Instant.ofEpochSecond(epoch)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return formatter.format(dateTime)
    }

    fun kelvinToFahrenheit(kelvin: Double): Double {
        return (kelvin - 273.15)
    }

    private fun callWeatherApi(city: String) {
        val retrofitBuilder = Retrofit.Builder().baseUrl(Utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val retrofitData = retrofitBuilder.getWeatherByCity(city, Utils.API_KEY)

        retrofitData.enqueue(object : Callback<Forcast?> {
            override fun onResponse(call: Call<Forcast?>, response: Response<Forcast?>) {
                val responseBody = response.body()

                if (responseBody != null) {

                    handleWeatherUI(response.body()!!, responseBody!!.list)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "There is no data for this city 1",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<Forcast?>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "There is no data for this city 2",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun handleWeatherUI(forecast: Forcast, weatherList: ArrayList<WeatherList>) {

        if (weatherList.isNotEmpty()) {
            val firstWeather = weatherList[0]

            binding.areaName.text = forecast.city!!.name

            adapter = MyAdapter(this@MainActivity, weatherList)
            recyclerView.adapter = adapter
        }
    }
}