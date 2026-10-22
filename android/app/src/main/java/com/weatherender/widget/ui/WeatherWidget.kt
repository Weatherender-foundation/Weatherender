package com.weatherender.widget.ui

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import com.weatherender.widget.data.WidgetPreferences
import com.weatherender.widget.data.JsonFileCache
import com.weatherender.widget.data.ApiClient
import com.weatherender.widget.data.WeatherResponseDto

class WeatherWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val city: String = WidgetPreferences.getCity(context, widgetId) ?: "Moscow"
        val cache = JsonFileCache(context)
        val cachedJson = cache.loadRawResponse(city)

        var data: WeatherResponseDto? = null
        if (!cachedJson.isNullOrBlank()) {
            try {
                data = ApiClient.parseWeatherResponse(cachedJson)
            } catch (e: Exception) {
                // Ignore parse errors, show default
            }
        }

        val tempC = data?.current?.tempC?.toInt() ?: 0
        val condition = data?.current?.condition?.text.orEmpty().ifBlank { "Clear" }
        val uv = data?.current?.uv?.toInt() ?: 0
        val humidity = data?.current?.humidity ?: 0
        val pressureMb = data?.current?.pressureMb ?: 0.0
        val pressureMmHg = (pressureMb * 0.750062).toInt()
        val cityName = data?.location?.name ?: city

        provideContent {
            Text(
                text = "$cityName: $tempC°C, $condition\nUV: $uv, Humidity: $humidity%, Pressure: $pressureMmHg mmHg"
            )
        }
    }
}

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()
}
