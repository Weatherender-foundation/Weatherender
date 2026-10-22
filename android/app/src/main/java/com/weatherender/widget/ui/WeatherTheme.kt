package com.weatherender.widget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class WeatherConditionTheme {
    SUNNY,
    RAINY,
    CLOUDY,
    THUNDER
}

object WeatherTheme {
    private val SunnyGradient = listOf(Color(0xFF4FACFE), Color(0xFF00F2FE))
    private val RainyGradient = listOf(Color(0xFF0F2027), Color(0xFF2C5364))
    private val CloudyGradient = listOf(Color(0xFF4B5D7D), Color(0xFFEDF2FF))
    private val ThunderGradient = listOf(Color(0xFF0A1220), Color(0xFF2C3E50))

    fun resolveTheme(conditionText: String): WeatherConditionTheme {
        val lower = conditionText.lowercase()
        return when {
            lower.contains("storm") || lower.contains("thunder") || lower.contains("lightning") -> WeatherConditionTheme.THUNDER
            lower.contains("cloud") || lower.contains("overcast") || lower.contains("fog") ||
                    lower.contains("mist") || lower.contains("haze") || lower.contains("snow") -> WeatherConditionTheme.CLOUDY
            lower.contains("rain") || lower.contains("drizzle") || lower.contains("shower") -> WeatherConditionTheme.RAINY
            else -> WeatherConditionTheme.SUNNY
        }
    }

    fun getGradient(theme: WeatherConditionTheme): List<Color> = when (theme) {
        WeatherConditionTheme.SUNNY -> SunnyGradient
        WeatherConditionTheme.RAINY -> RainyGradient
        WeatherConditionTheme.CLOUDY -> CloudyGradient
        WeatherConditionTheme.THUNDER -> ThunderGradient
    }

    fun getBackgroundBrush(theme: WeatherConditionTheme): Brush = Brush.verticalGradient(
        colors = getGradient(theme),
        startY = 0f,
        endY = 1f,
        tileMode = TileMode.Clamp
    )

    @Composable
    fun WeatherAppTheme(
        content: @Composable () -> Unit
    ) {
        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = Color(0xFF4FACFE),
                secondary = Color(0xFF00F2FE),
                tertiary = Color(0xFF7A5CFA),
                surface = Color.Transparent,
                background = Color.Transparent,
                onSurface = Color.White,
                onBackground = Color.White,
            ),
            content = content
        )
    }
}

@Composable
fun WeatherCard(
    modifier: Modifier = Modifier,
    elevation: Int = 0,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0x99000000),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.1f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(-1.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            content()
        }
    }
}

@Composable
fun StatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        androidx.compose.material3.Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Text(
        text = text,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}
