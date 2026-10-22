package com.weatherender.widget.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherender.widget.data.CurrentDto

@Composable
fun CurrentWeatherCard(
    current: CurrentDto,
    locationName: String,
    localTime: String,
    isMetric: Boolean,
    modifier: Modifier = Modifier
) {
    val temp = if (isMetric) current.tempC else current.tempF
    val tempUnit = if (isMetric) "°C" else "°F"
    val windSpeed = if (isMetric) (current.windKph ?: 0.0) else (current.windMph ?: 0.0)
    val windUnit = if (isMetric) "km/h" else "mph"
    val pressureMmHg = safeRoundToInt((current.pressureMb ?: 0.0) * 0.750062)

    WeatherCard(modifier = modifier.fillMaxWidth().wrapContentHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Eyebrow(text = "Weather dashboard")
            Title(text = "🌤 Weatherender forecast")

            TimeText(text = "Data for: $localTime")

            HeroCard(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    WeatherBadge()

                    Row(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CityMeta(text = locationName)
                            Row(
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WeatherConditionIcon(
                                    iconUrl = current.condition.icon,
                                    contentDescription = current.condition.text,
                                    modifier = Modifier.size(32.dp)
                                )
                                ConditionPill(text = current.condition.text)
                            }
                        }
                        HeroTemp(text = "${safeRoundToInt(temp)}$tempUnit")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatPill(
                            label = "UV",
                            value = safeDoubleString(current.uv)
                        )
                        StatPill(
                            label = "Humidity",
                            value = "${current.humidity ?: 0}%"
                        )
                        StatPill(
                            label = "Pressure",
                            value = "$pressureMmHg mmHg"
                        )
                    }
                }
            }

            BlockCard(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoHeading(text = "Current details")

                    DetailRow(
                        label = "🌡 Temperature",
                        value = "${safeRoundToInt(temp)}$tempUnit"
                    )
                    DetailRow(
                        label = "📝 Condition",
                        value = current.condition.text
                    )
                    DetailRow(
                        label = "💨 Wind",
                        value = "$windSpeed $windUnit"
                    )
                    DetailRow(
                        label = "☀️ UV Index",
                        value = safeDoubleString(current.uv)
                    )
                    DetailRow(
                        label = "⏲ Pressure",
                        value = "$pressureMmHg mmHg"
                    )
                    DetailRow(
                        label = "💧 Humidity",
                        value = "${current.humidity ?: 0}%"
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
