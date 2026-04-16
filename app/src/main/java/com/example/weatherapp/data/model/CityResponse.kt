package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class CityResponse(
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("country") val country: String
)