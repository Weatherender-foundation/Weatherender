package com.weatherender.widget.data

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
                }
            }
        }

        return runCatching {
            apiClient.fetchWeather(safeCity).getOrThrow()
        }.onSuccess { response ->
            cache.saveRawResponse(safeCity, apiClient.jsonToString(response))
        }
    }
}
