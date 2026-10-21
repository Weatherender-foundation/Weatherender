package com.weatherender.widget.data

import android.content.Context
import java.io.File

class JsonFileCache(private val context: Context) {
    private fun cacheFileFor(city: String): File {
        val safeCity = city.trim().ifBlank { "default" }
        val sanitized = safeCity.lowercase().replace("\\s+".toRegex(), "_")
        return File(context.cacheDir, "weather_$sanitized.json")
    }

    fun saveRawResponse(city: String, json: String) {
        val file = cacheFileFor(city)
        file.writeText(json)
    }

    fun loadRawResponse(city: String): String? {
        val file = cacheFileFor(city)
        return if (file.exists()) file.readText() else null
    }

    fun getLastModified(city: String): Long? {
        val file = cacheFileFor(city)
        return if (file.exists()) file.lastModified() else null
    }
}
