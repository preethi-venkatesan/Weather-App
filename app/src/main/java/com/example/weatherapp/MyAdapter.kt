package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

class MyAdapter(
    var context: Activity,
    private var list: List<WeatherList>,

    ) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var idToday: TextView = itemView.findViewById(R.id.idToday)
        var tvTime: TextView = itemView.findViewById(R.id.tvTime)
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var tempMode: TextView = itemView.findViewById(R.id.TempMode)
        var degree: TextView = itemView.findViewById(R.id.degree1)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.forcast, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = list[position]

        //To convert the Date to Day
//        val dateInMillis = currentItem.dt_txt.toLong() * 1000
//        val date = Date(dateInMillis)
//
//        val currentDay = SimpleDateFormat(
//            "EEEE",
//            Locale.getDefault()
//        ).format(date)
//
//        holder.idToday.text = currentDay

        var dayName : String = getDayFromEpoch(currentItem.dt)
        holder.idToday.text = dayName

        var correctTime : String = epochToDateTime(currentItem.dt)
        holder.tvTime.text = correctTime


        //description
        val weatherCondition = currentItem.weather.firstOrNull()
        weatherCondition?.let {
            holder.tempMode.text = it.description

            val iconResourceId = getWeatherIconResourceId(it.id)
            holder.imageView.setImageResource(iconResourceId)
        }

        //temperature
        var tempInKelvin : Double = currentItem.main.temp
        var celsius : Double = kelvinToFahrenheit(tempInKelvin)
        holder.degree.text = "$celsius°C"

    }

    fun kelvinToFahrenheit(kelvin: Double): Double {
        return (kelvin - 273.15)
    }

    //icon
    fun getWeatherIconResourceId(weatherId: Int): Int {
        return when (weatherId) {
            Utils.WEATHER_CLEAR_SKY -> R.drawable.sunny
            Utils.WEATHER_LIGHT_RAIN -> R.drawable.rain
            Utils.WEATHER_FEW_CLOUDS -> R.drawable.partly_cloudy
            Utils.WEATHER_SCATTERED_CLOUDS -> R.drawable.cloudy_clear_at_times
            else -> R.drawable.fog
        }

    }

    fun getDayFromEpoch(epoch: Long): String {
        val zoneId: ZoneId = ZoneId.systemDefault()
        val currentDateTime = Instant.ofEpochSecond(epoch).atZone(zoneId)
        val currentDate = currentDateTime.toLocalDate()
        val tomorrow = LocalDate.now().plusDays(1)

        return when {
            currentDate == LocalDate.now() -> "Today"
            currentDate == tomorrow -> "Tomorrow"
            else -> {
                // Get the day of the week
                val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                // Capitalize the first letter
                dayOfWeek.substring(0, 1).toUpperCase(Locale.getDefault()) + dayOfWeek.substring(1)
            }
        }
    }

    fun epochToDateTime(epoch: Long): String {
        val dateTime = Instant.ofEpochSecond(epoch)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return formatter.format(dateTime)
    }
}