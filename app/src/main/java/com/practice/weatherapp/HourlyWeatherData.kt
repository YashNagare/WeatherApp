package com.practice.weatherapp

data class HourlyWeatherData(
    val hourly: List<HourlyForecast>
)

data class HourlyForecast(
    val dt: Long,   // Time of the forecasted data, unix, UTC
    val temp: Double,
    val humidity: Int
)