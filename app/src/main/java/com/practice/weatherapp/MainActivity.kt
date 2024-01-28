package com.practice.weatherapp

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practice.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val apiKey = "API_KEY"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeatherAdapter
    private var dataList: ArrayList<DataClass> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.rv_hourly_weather)
        recyclerView.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

        adapter = WeatherAdapter(dataList)
        recyclerView.adapter = adapter

        fetchHourlyData(18.5204, 73.8567)
        fetchWeatherData("Pune")
        searchCity()

    }

    private fun updateUI(hourlyWeatherData: HourlyWeatherData) {

        dataList.clear() // Clear existing data before adding new data

        for (hourlyForecast in hourlyWeatherData.hourly) {
            val timestamp = time(hourlyForecast.dt)
            val temperature = hourlyForecast.temp - 273.15
            val temperatureCelsius = String.format("%.2f°C", temperature)

            dataList.add(DataClass(timestamp, temperatureCelsius))
        }
        adapter.notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    private fun searchCity() {
        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun getLatLong(cityName: String) {
        val gc = Geocoder(this, Locale.getDefault())

        val addresses = gc.getFromLocationName(cityName, 2)
        val address = addresses?.get(0)

        fetchHourlyData(address!!.latitude, address.longitude)

    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherData(cityName, apiKey, "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"

                    binding.tvTemperature.text = "$temperature °C"
                    binding.tvWeather.text = condition
                    binding.tvHumidity.text = "$humidity %"
                    binding.tvWindSpeed.text = "$windSpeed m/s"
                    binding.tvSunrise.text = "${time(sunRise)}"
                    binding.tvSunset.text = "${time(sunSet)}"
                    binding.tvSea.text = "$seaLevel hPa"
                    binding.tvCondition.text = condition
                    binding.tvCityName.text = "$cityName"

                    getLatLong(cityName)

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchHourlyData(latitude: Double, longitude: Double) {

        val weatherService = WeatherService()

        lifecycleScope.launch(Dispatchers.Main) {
            val hourlyWeatherData = withContext(Dispatchers.IO) {
                weatherService.getHourlyWeatherData(latitude, longitude, apiKey)
            }
            if (hourlyWeatherData != null) {
                updateUI(hourlyWeatherData)
            }
        }
    }

    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

}