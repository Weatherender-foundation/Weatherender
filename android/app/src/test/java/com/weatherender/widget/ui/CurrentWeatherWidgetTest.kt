package com.weatherender.widget.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherender.widget.data.JsonFileCache
import com.weatherender.widget.data.WeatherResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class CurrentWeatherWidgetTest {

    private lateinit var context: android.content.Context
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
    }

    @Test
    fun `resolveTheme returns SUNNY for clear condition`() {
        assertEquals(WeatherConditionTheme.SUNNY, WeatherTheme.resolveTheme("Clear"))
        assertEquals(WeatherConditionTheme.SUNNY, WeatherTheme.resolveTheme("Sunny"))
        assertEquals(WeatherConditionTheme.SUNNY, WeatherTheme.resolveTheme("clear sky"))
    }

    @Test
    fun `resolveTheme returns RAINY for rain conditions`() {
        assertEquals(WeatherConditionTheme.RAINY, WeatherTheme.resolveTheme("Rain"))
        assertEquals(WeatherConditionTheme.RAINY, WeatherTheme.resolveTheme("Light rain"))
        assertEquals(WeatherConditionTheme.RAINY, WeatherTheme.resolveTheme("Drizzle"))
        assertEquals(WeatherConditionTheme.RAINY, WeatherTheme.resolveTheme("Shower"))
    }

    @Test
    fun `resolveTheme returns CLOUDY for cloud conditions`() {
        assertEquals(WeatherConditionTheme.CLOUDY, WeatherTheme.resolveTheme("Cloudy"))
        assertEquals(WeatherConditionTheme.CLOUDY, WeatherTheme.resolveTheme("Overcast"))
        assertEquals(WeatherConditionTheme.CLOUDY, WeatherTheme.resolveTheme("Fog"))
        assertEquals(WeatherConditionTheme.CLOUDY, WeatherTheme.resolveTheme("Mist"))
        assertEquals(WeatherConditionTheme.CLOUDY, WeatherTheme.resolveTheme("Haze"))
    }

    @Test
    fun `resolveTheme returns CLOUDY for snow conditions`() {
        assertEquals(WeatherConditionTheme.CLOUDY, WeatherTheme.resolveTheme("Snow"))
        assertEquals(WeatherConditionTheme.CLOUDY, WeatherTheme.resolveTheme("Light snow"))
    }

    @Test
    fun `resolveTheme returns THUNDER for storm conditions`() {
        assertEquals(WeatherConditionTheme.THUNDER, WeatherTheme.resolveTheme("Thunderstorm"))
        assertEquals(WeatherConditionTheme.THUNDER, WeatherTheme.resolveTheme("Storm"))
        assertEquals(WeatherConditionTheme.THUNDER, WeatherTheme.resolveTheme("Lightning"))
        assertEquals(WeatherConditionTheme.THUNDER, WeatherTheme.resolveTheme("Thunder"))
    }

    @Test
    fun `resolveTheme precedence thunder over cloudy over rainy over sunny`() {
        // Thunder should win over cloudy
        assertEquals(WeatherConditionTheme.THUNDER, WeatherTheme.resolveTheme("Thunderstorm with clouds"))
        
        // Cloudy should win over rainy
        assertEquals(WeatherConditionTheme.CLOUDY, WeatherTheme.resolveTheme("Cloudy rain"))
        
        // Rainy should win over sunny
        assertEquals(WeatherConditionTheme.RAINY, WeatherTheme.resolveTheme("Rain and sun"))
    }

    @Test
    fun `getBackgroundBrush creates valid gradient brush`() {
        val sunnyBrush = WeatherTheme.getBackgroundBrush(WeatherConditionTheme.SUNNY)
        val rainyBrush = WeatherTheme.getBackgroundBrush(WeatherConditionTheme.RAINY)
        val cloudyBrush = WeatherTheme.getBackgroundBrush(WeatherConditionTheme.CLOUDY)
        val thunderBrush = WeatherTheme.getBackgroundBrush(WeatherConditionTheme.THUNDER)

        assertNotNull(sunnyBrush)
        assertNotNull(rainyBrush)
        assertNotNull(cloudyBrush)
        assertNotNull(thunderBrush)
    }

    @Test
    fun `pressure conversion from mb to mmHg`() {
        val pressureMb = 1013.25
        val pressureMmHg = (pressureMb * 0.750062).roundToInt()
        assertEquals(760, pressureMmHg)
    }

    @Test
    fun `pressure conversion edge cases`() {
        assertEquals(0, (0.0 * 0.750062).roundToInt())
        assertEquals(760, (1013.25 * 0.750062).roundToInt())
        assertEquals(737, (982.5 * 0.750062).roundToInt())
    }

    @Test
    fun `snow state parsing`() {
        // Test data
        val json = """
        {
            "location": {"name": "Test", "region": "", "country": "", "lat": 0.0, "lon": 0.0, "tz_id": "UTC", "localtime": "2024-01-01 12:00"},
            "current": {"temp_c": 0.0, "temp_f": 32.0, "condition": {"text": "Snow", "icon": "", "code": 1006}, "wind_kph": 10.0, "wind_mph": 6.2, "pressure_mb": 1013.0, "humidity": 80, "uv": 1.0},
            "forecast": {"forecastday": []},
            "snow_state": {"status": "snowing"},
            "snow_forecast": [],
            "attribution": {"copyright": "", "author": "", "license": "", "license_url": "", "trademarks_url": ""}
        }
        """.trimIndent()

        val dto = Json.decodeFromString<WeatherResponseDto>(json)
        assertEquals("snowing", dto.snowState.status)
    }
}