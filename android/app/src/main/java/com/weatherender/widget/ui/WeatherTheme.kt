package com.weatherender.widget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class WeatherConditionTheme {
    SUNNY,
    RAINY,
    CLOUDY,
    THUNDER
}

internal fun safeGradientColors(colors: List<Color>): List<Color> = when (colors.size) {
    0 -> listOf(Color.Transparent, Color.Transparent)
    1 -> listOf(colors.first(), colors.first())
    else -> colors
}

object WeatherTheme {
    private val SunnyGradient = listOf(Color(0xFF4FACFE), Color(0xFF00F2FE))
    private val RainyGradient = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    private val CloudyGradient = listOf(Color(0xFF4B5D7D), Color(0xFFB5C7DB), Color(0xFFEDF2FF))
    private val ThunderGradient = listOf(Color(0xFF0A1220), Color(0xFF1E2A39), Color(0xFF2C3E50))

    private val SunnyGradientStops = listOf(0f, 1f)
    private val RainyGradientStops = listOf(0f, 0.35f, 1f)
    private val CloudyGradientStops = listOf(0f, 0.52f, 1f)
    private val ThunderGradientStops = listOf(0f, 0.32f, 1f)

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

    fun getGradientStops(theme: WeatherConditionTheme): List<Float> = when (theme) {
        WeatherConditionTheme.SUNNY -> SunnyGradientStops
        WeatherConditionTheme.RAINY -> RainyGradientStops
        WeatherConditionTheme.CLOUDY -> CloudyGradientStops
        WeatherConditionTheme.THUNDER -> ThunderGradientStops
    }

    fun getBackgroundBrush(theme: WeatherConditionTheme): Brush {
        val colors = safeGradientColors(getGradient(theme))
        val themeStops = getGradientStops(theme)
        val stops = if (themeStops.size == colors.size) {
            themeStops
        } else {
            List(colors.size) { index -> index.toFloat() / (colors.size - 1) }
        }
        val colorStops = colors.zip(stops).map { (color, stop) -> stop to color }.toTypedArray()
        return Brush.linearGradient(
            colorStops = colorStops,
            start = Offset(0f, 0f),
            end = Offset.Infinite,
            tileMode = TileMode.Clamp
        )
    }

    val darkBackgroundBrush: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)),
        start = Offset.Zero,
        end = Offset.Infinite,
        tileMode = TileMode.Clamp
    )

    val frostedGlassCardBackground = Color(0x121B26).copy(alpha = 0.34f)
    val frostedGlassCardBorder = Color.White.copy(alpha = 0.18f)
    val frostedGlassBlockBackground = Color.White.copy(alpha = 0.06f)
    val frostedGlassBlockBorder = Color.White.copy(alpha = 0.12f)
    val frostedGlassHeroBackgroundStart = Color.White.copy(alpha = 0.12f)
    val frostedGlassHeroBackgroundEnd = Color.White.copy(alpha = 0.04f)
    val frostedGlassHeroBorder = Color.White.copy(alpha = 0.14f)
    val frostedGlassStatPillBackground = Color(0x07111C).copy(alpha = 0.15f)
    val frostedGlassStatPillBorder = Color.White.copy(alpha = 0.08f)
    val frostedGlassUnitToggleBackground = Color.White.copy(alpha = 0.12f)
    val frostedGlassUnitToggleBorder = Color.White.copy(alpha = 0.18f)
    val frostedGlassConditionPillBackground = Color(0x50B0FF).copy(alpha = 0.16f)
    val frostedGlassConditionPillBorder = Color(0xA0E1FF).copy(alpha = 0.35f)
    val frostedGlassWeatherBadgeBackground = Color.White.copy(alpha = 0.10f)
    val frostedGlassWeatherBadgeBorder = Color.White.copy(alpha = 0.12f)
    val frostedGlassTableContainerBackground = Color.White.copy(alpha = 0.04f)
    val frostedGlassTableContainerBorder = Color.White.copy(alpha = 0.12f)
    val frostedGlassDayCardBackground = Color.White.copy(alpha = 0.06f)
    val frostedGlassDayCardBorder = Color.White.copy(alpha = 0.10f)

    val cardCornerRadius = 30.dp
    val blockCornerRadius = 22.dp
    val heroCornerRadius = 22.dp
    val statPillCornerRadius = 14.dp
    val unitToggleCornerRadius = 999.dp
    val conditionPillCornerRadius = 999.dp
    val weatherBadgeCornerRadius = 999.dp
    val tableContainerCornerRadius = 16.dp
    val dayCardCornerRadius = 16.dp
    val inputCornerRadius = 14.dp
    val buttonCornerRadius = 14.dp

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
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = WeatherTheme.frostedGlassCardBackground,
                shape = RoundedCornerShape(WeatherTheme.cardCornerRadius)
            )
            .border(1.dp, WeatherTheme.frostedGlassCardBorder, RoundedCornerShape(WeatherTheme.cardCornerRadius))
    ) {
        content()
    }
}

@Composable
fun BlockCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = WeatherTheme.frostedGlassBlockBackground,
                shape = RoundedCornerShape(WeatherTheme.blockCornerRadius)
            )
            .border(1.dp, WeatherTheme.frostedGlassBlockBorder, RoundedCornerShape(WeatherTheme.blockCornerRadius))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun HeroCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = WeatherTheme.frostedGlassHeroBackgroundStart,
                shape = RoundedCornerShape(WeatherTheme.heroCornerRadius)
            )
            .border(1.dp, WeatherTheme.frostedGlassHeroBorder, RoundedCornerShape(WeatherTheme.heroCornerRadius))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun StatPillCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = WeatherTheme.frostedGlassStatPillBackground,
                shape = RoundedCornerShape(WeatherTheme.statPillCornerRadius)
            )
            .border(1.dp, WeatherTheme.frostedGlassStatPillBorder, RoundedCornerShape(WeatherTheme.statPillCornerRadius))
            .padding(12.dp, 14.dp)
    ) {
        content()
    }
}
