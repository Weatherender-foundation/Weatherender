package com.weatherender.widget.ui

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherUiUtilsTest {
    @Test
    fun `ensures gradient colors contain at least two colors`() {
        val color = Color(0xFF123456)

        assertEquals(
            listOf(Color.Transparent, Color.Transparent),
            safeGradientColors(emptyList())
        )
        assertEquals(
            listOf(color, color),
            safeGradientColors(listOf(color))
        )
        assertEquals(
            listOf(color, Color.White),
            safeGradientColors(listOf(color, Color.White))
        )
    }

    @Test
    fun `normalizes protocol-relative condition icon URLs`() {
        assertEquals(
            "https://cdn.weatherapi.com/weather/64x64/day/113.png",
            normalizeConditionIconUrl("//cdn.weatherapi.com/weather/64x64/day/113.png")
        )
    }

    @Test
    fun `preserves absolute condition icon URLs`() {
        assertEquals(
            "https://cdn.weatherapi.com/weather/64x64/day/113.png",
            normalizeConditionIconUrl("https://cdn.weatherapi.com/weather/64x64/day/113.png")
        )
    }

    @Test
    fun `rounds non-finite values to zero`() {
        assertEquals(0, safeRoundToInt(Double.NaN))
        assertEquals(0, safeRoundToInt(Double.POSITIVE_INFINITY))
        assertEquals(0, safeRoundToInt(Double.NEGATIVE_INFINITY))
    }

    @Test
    fun `rounds finite values normally`() {
        assertEquals(13, safeRoundToInt(12.6))
        assertEquals(-13, safeRoundToInt(-12.6))
    }

    @Test
    fun `formats non-finite values as zero`() {
        assertEquals("0.0", safeDoubleString(Double.NaN))
        assertEquals("0.0", safeDoubleString(Double.POSITIVE_INFINITY))
        assertEquals("0.0", safeDoubleString(Double.NEGATIVE_INFINITY))
        assertEquals("3.5", safeDoubleString(3.5))
    }
}
