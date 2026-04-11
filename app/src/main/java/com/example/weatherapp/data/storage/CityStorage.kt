package com.example.weatherapp.data.storage

import android.content.Context
import com.example.weatherapp.data.model.SavedCity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class CityStorage(context: Context) {
    private val file = File(context.filesDir, "cities.json")
    private val gson = Gson()

    fun getCities(): List<SavedCity> {
        if (!file.exists()) return emptyList()

        return try {
            val json = file.readText()
            val type = object : TypeToken<List<SavedCity>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun saveCities(cities: List<SavedCity>) {
        val json = gson.toJson(cities)
        file.writeText(json)
    }
}