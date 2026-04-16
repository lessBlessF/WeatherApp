package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.CityResponse
import com.example.weatherapp.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiNinjasService {
    @GET("v1/city")
    suspend fun getCity(
        @Query("name") name: String,
        @Header("X-Api-Key") apiKey: String = Constants.API_NINJAS_KEY
    ): List<CityResponse>
}