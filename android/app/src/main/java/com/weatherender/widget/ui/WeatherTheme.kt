package com.weatherender.widget.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF3A7BD5),
    secondary = androidx.compose.ui.graphics.Color(0xFF00D2FF),
    tertiary = androidx.compose.ui.graphics.Color(0xFF7A5CFA),
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF8EC5FF),
    secondary = androidx.compose.ui.graphics.Color(0xFF6EE7FF),
    tertiary = androidx.compose.ui.graphics.Color(0xFFA78BFA),
)

@Composable
fun WeatherAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (false) DarkColors else LightColors,
        content = content,
    )
}
