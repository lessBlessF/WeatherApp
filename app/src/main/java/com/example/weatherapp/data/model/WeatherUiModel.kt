package com.example.weatherapp.data.model

data class WeatherUiModel(
    val cityName: String,
    val country: String,
    val currentTemperature: Double,
    val dailyForecasts: List<DailyForecast>
)

data class DailyForecast(
    val date: String,
    val dayOfWeek: String,
    val hourly: List<HourlyForecast>
)

data class HourlyForecast(
    val time: String,
    val temperature: Double
)