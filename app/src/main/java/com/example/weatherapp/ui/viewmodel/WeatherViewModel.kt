package com.example.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.model.CityResponse
import com.example.weatherapp.data.model.WeatherUiModel
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherList = MutableStateFlow<List<WeatherUiModel>>(emptyList())
    val weatherList: StateFlow<List<WeatherUiModel>> = _weatherList.asStateFlow()

    private val _citySuggestions = MutableStateFlow<List<CityResponse>>(emptyList())
    val citySuggestions: StateFlow<List<CityResponse>> = _citySuggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorMessage: StateFlow<Int?> = _errorMessage.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSavedCitiesWeather()
    }

    private fun loadSavedCitiesWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            val savedCities = repository.getSavedCities()
            val weatherData = savedCities.mapNotNull { repository.getWeatherForSavedCity(it) }
            _weatherList.value = weatherData
            _isLoading.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.length < 2) {
            _citySuggestions.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            _citySuggestions.value = repository.searchCities(query)
        }
    }

    fun selectCityFromSuggestions(city: CityResponse) {
        viewModelScope.launch {
            _citySuggestions.value = emptyList()
            _isLoading.value = true
            _errorMessage.value = null

            val savedCity = repository.addCityToStorage(city)
            val weather = repository.getWeatherForSavedCity(savedCity)

            if (weather != null) {
                if (_weatherList.value.none { it.cityName == weather.cityName }) {
                    _weatherList.value = _weatherList.value + weather
                } else {
                    _errorMessage.value = R.string.error_already_exists
                }
            } else {
                _errorMessage.value = R.string.error_load_failed
            }

            _isLoading.value = false
        }
    }

    fun removeCity(cityName: String) {
        repository.removeCityFromStorage(cityName)
        _weatherList.value = _weatherList.value.filter { it.cityName != cityName }
    }
}

class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}