package com.example.weatherapp.data.api

import com.example.weatherapp.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val apiNinjasService: ApiNinjasService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.API_NINJAS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiNinjasService::class.java)
    }

    val openMeteoService: OpenMeteoService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.OPEN_METEO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoService::class.java)
    }
}