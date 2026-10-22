package com.weatherender.widget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherender.widget.data.HourDto
import com.weatherender.widget.data.LocationDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

@Composable
fun HourlyForecastTable(
    forecastHours: List<HourDto>,
    location: LocationDto,
    isMetric: Boolean,
    modifier: Modifier = Modifier
) {
    val hourlyData = getNext24Hours(forecastHours, location)

    BlockCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⏰ 24-Hour Forecast",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )

            TableContainer() {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    HeaderRow()
                    hourlyData.forEachIndexed { index, hour ->
                        HourRow(hour = hour, isMetric = isMetric, isOdd = index % 2 == 1, isLast = index == hourlyData.lastIndex)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 12.dp)
            .background(Color(0xFFE4F0FF).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell(text = "Time")
        HeaderCell(text = "Temperature")
        HeaderCell(text = "Precipitation %")
        HeaderCell(text = "UV Index")
        HeaderCell(text = "Pressure")
    }
}

@Composable
private fun HeaderCell(text: String) {
    Text(
        text = text,
        color = Color(0xFFEAF6FF),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun HourRow(
    hour: HourDto,
    isMetric: Boolean,
    isOdd: Boolean,
    isLast: Boolean
) {
    val temp = if (isMetric) (hour.tempC ?: 0.0) else (hour.tempF ?: 0.0)
    val tempUnit = if (isMetric) "°C" else "°F"
    val pressureMmHg = ((hour.pressureMb ?: 0.0) * 0.750062).roundToInt()
    val timeStr = formatHour(hour.time ?: "")

    val rowBgColor = if (isOdd) {
        Color.White.copy(alpha = 0.04f)
    } else {
        Color.White.copy(alpha = 0.02f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 12.dp)
            .background(rowBgColor, RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DataCell(text = timeStr, isFirst = true)
        DataCell(text = "${temp.roundToInt()}$tempUnit")
        DataCell(text = "${hour.chanceOfRain ?: 0}%")
        DataCell(text = (hour.uv ?: 0.0).toString())
        DataCell(text = "$pressureMmHg mmHg", isLast = true)
    }
}

@Composable
private fun DataCell(
    text: String,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    Text(
        text = text,
        color = Color(0xEDF5FF).copy(alpha = 0.92f),
        fontSize = 15.sp,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .fillMaxWidth(),
        textAlign = if (isFirst) TextAlign.Start else TextAlign.Center
    )
}

private fun getNext24Hours(forecastHours: List<HourDto>, location: LocationDto): List<HourDto> {
    val localTimeStr = location.localTime
    val tzId = location.tzId

    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone(tzId)

    var currentLocalTime: Date
    try {
        currentLocalTime = formatter.parse(localTimeStr) ?: Date()
    } catch (e: Exception) {
        currentLocalTime = Date()
    }

    val currentCal = java.util.Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone(tzId)
        time = currentLocalTime
    }
    val currentHour = currentCal[java.util.Calendar.HOUR_OF_DAY]
    val currentDayOfYear = currentCal[java.util.Calendar.DAY_OF_YEAR]

    return forecastHours
        .filter { hour ->
            val hourTime = parseHourTime(hour.time ?: "")
            val hourCal = java.util.Calendar.getInstance().apply {
                timeZone = TimeZone.getTimeZone(tzId)
                time = hourTime
            }
            val hourOfDay = hourCal[java.util.Calendar.HOUR_OF_DAY]
            val dayOfYear = hourCal[java.util.Calendar.DAY_OF_YEAR]

            dayOfYear > currentDayOfYear || (dayOfYear == currentDayOfYear && hourOfDay >= currentHour)
        }
        .take(24)
}

private fun parseHourTime(timeStr: String): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return try {
        formatter.parse(timeStr) ?: Date()
    } catch (e: Exception) {
        Date()
    }
}

private fun formatHour(timeStr: String): String {
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val outputFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return try {
        outputFormatter.format(inputFormatter.parse(timeStr) ?: Date())
    } catch (e: Exception) {
        "--:--"
    }
}
