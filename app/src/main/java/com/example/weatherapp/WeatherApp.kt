package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.storage.CityStorage

class WeatherApp : Application() {
    val repository by lazy {
        val storage = CityStorage(this)
        WeatherRepository(storage)
    }
}