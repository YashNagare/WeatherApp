package com.practice.weatherapp

import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class WeatherService() {

    private val client = OkHttpClient()
    private val gson = Gson()

    fun getHourlyWeatherData(
        latitude: Double,
        longitude: Double,
        apiKey: String,
    ): HourlyWeatherData? {

        val url =
            "https://api.openweathermap.org/data/2.5/onecall?lat=$latitude&lon=$longitude&exclude=current,minutely,daily&appid=$apiKey"

        Log.e("Hello", url)


        val request = Request.Builder()
            .url(url)
            .build()

        try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            return gson.fromJson(body, HourlyWeatherData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}

