package com.weatherender.widget.data

import com.weatherender.widget.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object ApiClient {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    fun jsonToString(value: WeatherResponseDto): String =
        json.encodeToString(WeatherResponseDto.serializer(), value)

    fun parseWeatherResponse(rawJson: String): WeatherResponseDto =
        json.decodeFromString(rawJson)

    suspend fun fetchWeather(city: String): Result<WeatherResponseDto> {
        return try {
            val safeCity = city.trim()
            require(safeCity.isNotBlank()) { "City must not be blank." }

            val encodedCity = URLEncoder.encode(safeCity, Charsets.UTF_8.name())
            val url = "${BuildConfig.API_BASE_URL.trimEnd('/')}/api/v2/weather?city=$encodedCity"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val rawJson = withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("Weather request failed with HTTP ${response.code}")
                    }
                    response.body?.string()
                        ?: throw IllegalStateException("Weather response body was empty.")
                }
            }

            Result.success(parseWeatherResponse(rawJson))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
