package com.weatherender.widget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherender.widget.data.CurrentDto
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherCard(
    current: CurrentDto,
    locationName: String,
    localTime: String,
    isMetric: Boolean,
    theme: WeatherConditionTheme,
    modifier: Modifier = Modifier
) {
    val temp = if (isMetric) current.tempC else current.tempF
    val tempUnit = if (isMetric) "°C" else "°F"
    val windSpeed = if (isMetric) current.windKph else current.windMph
    val windUnit = if (isMetric) "km/h" else "mph"
    val pressureMmHg = (current.pressureMb * 0.750062).roundToInt()

    WeatherCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Eyebrow(text = "Weather dashboard")
            Title(text = "🌤 Weatherender forecast")

            TimeText(text = "Data for: $localTime")

            HeroCard() {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    WeatherBadge()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CityMeta(text = locationName)
                            ConditionPill(text = current.condition.text)
                        }
                        HeroTemp(text = "${temp.roundToInt()}$tempUnit")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatPill(
                            label = "UV",
                            value = current.uv.toString()
                        )
                        StatPill(
                            label = "Humidity",
                            value = "${current.humidity}%"
                        )
                        StatPill(
                            label = "Pressure",
                            value = "$pressureMmHg mmHg"
                        )
                    }
                }
            }

            BlockCard() {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoHeading(text = "Current details")

                    DetailRow(
                        label = "🌡 Temperature",
                        value = "${temp.roundToInt()}$tempUnit"
                    )
                    DetailRow(
                        label = "📝 Condition",
                        value = current.condition.text
                    )
                    DetailRow(
                        label = "☀️ UV Index",
                        value = current.uv.toString()
                    )
                    DetailRow(
                        label = "⏲ Pressure",
                        value = "$pressureMmHg mmHg"
                    )
                    DetailRow(
                        label = "💧 Humidity",
                        value = "${current.humidity}%"
                    )
                    DetailRow(
                        label = "⛷️ Snow Condition",
                        value = "N/A",
                        isLast = true
                    )
                }
            }
        }
    }
}
