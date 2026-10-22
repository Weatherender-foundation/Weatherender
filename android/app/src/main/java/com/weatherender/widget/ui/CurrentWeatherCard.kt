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

    WeatherCard(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = locationName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Updated: $localTime",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(
                            id = getConditionIconRes(current.condition.code, theme)
                        ),
                        contentDescription = current.condition.text,
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${temp.roundToInt()}$tempUnit",
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = current.condition.text,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Feels like ${if (isMetric) current.tempC.roundToInt() else current.tempF.roundToInt()}$tempUnit",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatPill(
                    label = "Humidity",
                    value = "${current.humidity}%"
                )
                StatPill(
                    label = "Wind",
                    value = "${windSpeed.roundToInt()} $windUnit ${current.windDir}"
                )
                StatPill(
                    label = "Pressure",
                    value = "$pressureMmHg mmHg"
                )
                StatPill(
                    label = "UV",
                    value = current.uv.toString()
                )
            }
        }
    }
}

private fun getConditionIconRes(code: Int, theme: WeatherConditionTheme): Int {
    return when (theme) {
        WeatherConditionTheme.SUNNY -> android.R.drawable.ic_menu_report_image
        WeatherConditionTheme.RAINY -> android.R.drawable.ic_menu_report_image
        WeatherConditionTheme.CLOUDY -> android.R.drawable.ic_menu_report_image
        WeatherConditionTheme.THUNDER -> android.R.drawable.ic_menu_report_image
    }
}
