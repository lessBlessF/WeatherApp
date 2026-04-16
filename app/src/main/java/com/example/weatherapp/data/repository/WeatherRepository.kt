package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.model.CityResponse
import com.example.weatherapp.data.model.DailyForecast
import com.example.weatherapp.data.model.HourlyForecast
import com.example.weatherapp.data.model.SavedCity
import com.example.weatherapp.data.model.WeatherUiModel
import com.example.weatherapp.data.storage.CityStorage
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherRepository(private val storage: CityStorage) {
    private val cityApi = RetrofitClient.apiNinjasService
    private val weatherApi = RetrofitClient.openMeteoService

    fun getSavedCities(): List<SavedCity> = storage.getCities()

    fun addCityToStorage(city: CityResponse): SavedCity {
        val savedCity = SavedCity(city.name, city.country, city.latitude, city.longitude)
        val currentCities = storage.getCities().toMutableList()
        if (currentCities.none { it.cityName == savedCity.cityName }) {
            currentCities.add(savedCity)
            storage.saveCities(currentCities)
        }
        return savedCity
    }

    fun removeCityFromStorage(cityName: String) {
        val currentCities = storage.getCities().toMutableList()
        currentCities.removeAll { it.cityName == cityName }
        storage.saveCities(currentCities)
    }

    suspend fun searchCities(query: String): List<CityResponse> {
        return try {
            cityApi.getCity(name = query).take(5)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getWeatherForSavedCity(savedCity: SavedCity): WeatherUiModel? {
        return try {
            val weather = weatherApi.getWeather(
                latitude = savedCity.latitude,
                longitude = savedCity.longitude
            )

            val times = weather.hourly.time
            val temps = weather.hourly.temperatures

            val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val sdfDate = SimpleDateFormat("d MMMM", Locale.getDefault())
            val sdfDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault())
            val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())

            val dailyMap = mutableMapOf<String, MutableList<HourlyForecast>>()
            val dayOfWeekMap = mutableMapOf<String, String>()

            for (i in times.indices) {
                if (i < temps.size) {
                    val dateObj = sdfInput.parse(times[i]) ?: continue

                    val dateStr = sdfDate.format(dateObj)
                    val dayOfWeekStr = sdfDayOfWeek.format(dateObj).replaceFirstChar { it.uppercase() }
                    val timeStr = sdfTime.format(dateObj)

                    if (!dailyMap.containsKey(dateStr)) {
                        dailyMap[dateStr] = mutableListOf()
                        dayOfWeekMap[dateStr] = dayOfWeekStr
                    }
                    dailyMap[dateStr]?.add(HourlyForecast(timeStr, temps[i]))
                }
            }

            val dailyForecasts = dailyMap.map { (date, hourlyList) ->
                DailyForecast(
                    date = date,
                    dayOfWeek = dayOfWeekMap[date] ?: "",
                    hourly = hourlyList
                )
            }

            WeatherUiModel(
                cityName = savedCity.cityName,
                country = savedCity.country,
                currentTemperature = temps.firstOrNull() ?: 0.0,
                dailyForecasts = dailyForecasts
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}