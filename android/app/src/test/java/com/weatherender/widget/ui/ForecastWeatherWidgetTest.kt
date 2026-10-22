package com.weatherender.widget.ui

import com.weatherender.widget.data.DayDto
import com.weatherender.widget.data.ForecastDayDto
import com.weatherender.widget.data.ConditionDto
import kotlin.math.roundToInt
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.text.SimpleDateFormat
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class ForecastWeatherWidgetTest {

    @Test
    fun `gusts fallback uses gust_kph when available`() {
        val day = DayDto(
            avgTempC = 15.0,
            maxTempC = 20.0,
            minTempC = 10.0,
            maxWindKph = 20.0,
            totalPrecipMm = 5.0,
            dailyChanceOfRain = 60,
            dailyChanceOfSnow = 0,
            dailyWillItSnow = 0,
            uv = 5.0,
            gustKph = 35.0,
            condition = null
        )

        val gustKph = (day.gustKph ?: (day.maxWindKph ?: 0.0) * 1.2).roundToInt()
        assertEquals(35, gustKph)
    }

    @Test
    fun `gusts fallback calculates from maxWindKph when gust_kph is null`() {
        val day = DayDto(
            avgTempC = 15.0,
            maxTempC = 20.0,
            minTempC = 10.0,
            maxWindKph = 20.0,
            totalPrecipMm = 5.0,
            dailyChanceOfRain = 60,
            dailyChanceOfSnow = 0,
            dailyWillItSnow = 0,
            uv = 5.0,
            gustKph = null,
            condition = null
        )

        val gustKph = (day.gustKph ?: (day.maxWindKph ?: 0.0) * 1.2).roundToInt()
        assertEquals(24, gustKph) // 20 * 1.2 = 24
    }

    @Test
    fun `gusts fallback handles both null`() {
        val day = DayDto(
            avgTempC = 15.0,
            maxTempC = 20.0,
            minTempC = 10.0,
            maxWindKph = null,
            totalPrecipMm = 5.0,
            dailyChanceOfRain = 60,
            dailyChanceOfSnow = 0,
            dailyWillItSnow = 0,
            uv = 5.0,
            gustKph = null,
            condition = null
        )

        val gustKph = (day.gustKph ?: (day.maxWindKph ?: 0.0) * 1.2).roundToInt()
        assertEquals(0, gustKph)
    }

    @Test
    fun `precip chance uses max of rain and snow`() {
        val day1 = DayDto(
            avgTempC = 15.0, maxTempC = 20.0, minTempC = 10.0,
            maxWindKph = 20.0, totalPrecipMm = 5.0,
            dailyChanceOfRain = 60, dailyChanceOfSnow = 30, dailyWillItSnow = 0,
            uv = 5.0, gustKph = 25.0, condition = null
        )
        val precipChance1 = maxOf(day1.dailyChanceOfRain ?: 0, day1.dailyChanceOfSnow ?: 0)
        assertEquals(60, precipChance1)

        val day2 = DayDto(
            avgTempC = 15.0, maxTempC = 20.0, minTempC = 10.0,
            maxWindKph = 20.0, totalPrecipMm = 5.0,
            dailyChanceOfRain = 20, dailyChanceOfSnow = 70, dailyWillItSnow = 1,
            uv = 5.0, gustKph = 25.0, condition = null
        )
        val precipChance2 = maxOf(day2.dailyChanceOfRain ?: 0, day2.dailyChanceOfSnow ?: 0)
        assertEquals(70, precipChance2)
    }

    @Test
    fun `snow state parsing from daily_will_it_snow`() {
        val dayWithSnow = DayDto(
            avgTempC = 15.0, maxTempC = 20.0, minTempC = 10.0,
            maxWindKph = 20.0, totalPrecipMm = 5.0,
            dailyChanceOfRain = 20, dailyChanceOfSnow = 70, dailyWillItSnow = 1,
            uv = 5.0, gustKph = 25.0, condition = null
        )
        val snowState1 = dayWithSnow.dailyWillItSnow == 1
        assertTrue(snowState1)

        val dayWithoutSnow = DayDto(
            avgTempC = 15.0, maxTempC = 20.0, minTempC = 10.0,
            maxWindKph = 20.0, totalPrecipMm = 5.0,
            dailyChanceOfRain = 60, dailyChanceOfSnow = 0, dailyWillItSnow = 0,
            uv = 5.0, gustKph = 25.0, condition = null
        )
        val snowState2 = dayWithoutSnow.dailyWillItSnow == 1
        assertFalse(snowState2)
    }

    @Test
    fun `date formatting DD MM YYYY`() {
        val inputDate = "2024-01-15"
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsed = sdf.parse(inputDate)
        val output = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(parsed!!)
        assertEquals("15.01.2024", output)
    }

    @Test
    fun `date formatting handles various dates`() {
        val testCases = listOf(
            "2024-01-01" to "01.01.2024",
            "2024-12-31" to "31.12.2024",
            "2024-06-15" to "15.06.2024"
        )

        val sdfIn = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfOut = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        testCases.forEach { (input, expected) ->
            val parsed = sdfIn.parse(input)
            val output = sdfOut.format(parsed!!)
            assertEquals(expected, output)
        }
    }

    @Test
    fun `forecast day card data extraction`() {
        val day = ForecastDayDto(
            date = "2024-01-15",
            dateEpoch = 1705276800,
            day = DayDto(
                avgTempC = 12.5,
                maxTempC = 18.0,
                minTempC = 7.0,
                maxWindKph = 15.0,
                maxWindMph = 9.3,
                totalPrecipMm = 3.2,
                dailyChanceOfRain = 45,
                dailyChanceOfSnow = 0,
                dailyWillItSnow = 0,
                totalSnowCm = 0.0,
                uv = 4.0,
                gustKph = 22.0,
                gustMph = 13.7,
                condition = ConditionDto(text = "Partly cloudy", icon = "", code = 1003)
            ),
            hour = emptyList()
        )

        val dateDisplay = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsed = sdf.parse(day.date)
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(parsed!!)
        } catch (e: Exception) {
            day.date
        }

        val avgTempC = day.day.avgTempC?.roundToInt() ?: 0
        val precipMm = day.day.totalPrecipMm ?: 0.0
        val precipChance = maxOf(day.day.dailyChanceOfRain ?: 0, day.day.dailyChanceOfSnow ?: 0)
        val uv = day.day.uv?.roundToInt() ?: 0
        val windKph = day.day.maxWindKph?.roundToInt() ?: 0
        val gustKph = (day.day.gustKph ?: (day.day.maxWindKph ?: 0.0) * 1.2).roundToInt()
        val snowState = day.day.dailyWillItSnow == 1

        assertEquals("15.01.2024", dateDisplay)
        assertEquals(13, avgTempC)
        assertEquals(3.2, precipMm, 0.01)
        assertEquals(45, precipChance)
        assertEquals(4, uv)
        assertEquals(15, windKph)
        assertEquals(22, gustKph)
        assertFalse(snowState)
    }

    @Test
    fun `theme resolution for forecast uses first day condition`() {
        // Test the theme resolution logic used in the widget
        val sunnyCondition = "Sunny"
        val rainyCondition = "Light rain"
        val cloudyCondition = "Overcast"
        val thunderCondition = "Thunderstorm"

        assertEquals(WeatherConditionTheme.SUNNY, WeatherTheme.resolveTheme(sunnyCondition))
        assertEquals(WeatherConditionTheme.RAINY, WeatherTheme.resolveTheme(rainyCondition))
        assertEquals(WeatherConditionTheme.CLOUDY, WeatherTheme.resolveTheme(cloudyCondition))
        assertEquals(WeatherConditionTheme.THUNDER, WeatherTheme.resolveTheme(thunderCondition))
    }
}