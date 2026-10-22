package com.weatherender.widget.data

import android.util.Log
import kotlin.runCatching

class WeatherRepository(
    private val cache: JsonFileCache,
    private val apiClient: ApiClient = ApiClient,
) {
    suspend fun getWeather(city: String, forceRefresh: Boolean = false): Result<WeatherResponseDto> {
        val safeCity = city.trim()
        if (!forceRefresh) {
            val cachedJson = cache.loadRawResponse(safeCity)
            if (!cachedJson.isNullOrBlank()) {
                return runCatching {
                    apiClient.parseWeatherResponse(cachedJson)
                }.onFailure { e ->
                    Log.e("WeatherApi", "Failed to parse cached JSON", e)
                }
            }
        }

        return runCatching {
            apiClient.fetchWeather(safeCity).getOrThrow()
        }.onSuccess { response ->
            cache.saveRawResponse(safeCity, apiClient.jsonToString(response))
        }.onFailure { e ->
            Log.e("WeatherApi", "Failed to fetch or parse weather", e)
        }
    }

    fun parseCachedResponse(json: String): WeatherResponseDto {
        return apiClient.parseWeatherResponse(json)
    }
}
