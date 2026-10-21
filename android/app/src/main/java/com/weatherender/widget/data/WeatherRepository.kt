package com.weatherender.widget.data

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

        val fetched = apiClient.fetchWeather(safeCity)
        fetched.onSuccess { response ->
            cache.saveRawResponse(safeCity, apiClient.jsonToString(response))
        }
        return fetched
    }
}
