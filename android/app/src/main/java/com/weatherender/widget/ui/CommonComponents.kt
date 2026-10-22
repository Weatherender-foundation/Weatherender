package com.weatherender.widget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Composable
fun UnitToggle(
    isMetric: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = if (isMetric) {
        "°C / °F | km/h / mph"
    } else {
        "°F / °C | mph / km/h"
    }

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onToggle() }
            .background(
                color = WeatherTheme.frostedGlassUnitToggleBackground,
                shape = RoundedCornerShape(WeatherTheme.unitToggleCornerRadius)
            )
            .border(1.dp, WeatherTheme.frostedGlassUnitToggleBorder, RoundedCornerShape(WeatherTheme.unitToggleCornerRadius))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color(0xFFEDF8FF),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun WeatherBadge(
    text: String = "Live conditions",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = WeatherTheme.frostedGlassWeatherBadgeBackground,
                shape = RoundedCornerShape(WeatherTheme.weatherBadgeCornerRadius)
            )
            .border(1.dp, WeatherTheme.frostedGlassWeatherBadgeBorder, RoundedCornerShape(WeatherTheme.weatherBadgeCornerRadius))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFEFF7FF).copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.08.em
        )
    }
}

@Composable
fun ConditionPill(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = WeatherTheme.frostedGlassConditionPillBackground,
                shape = RoundedCornerShape(WeatherTheme.conditionPillCornerRadius)
            )
            .border(1.dp, WeatherTheme.frostedGlassConditionPillBorder, RoundedCornerShape(WeatherTheme.conditionPillCornerRadius))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFEBF8FF),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun Eyebrow(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = Color(0xFFE6F3FF).copy(alpha = 0.8f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.2.em,
        modifier = modifier
    )
}

@Composable
fun Title(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color(0xFFF2F8FF),
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = -0.04.em,
        modifier = modifier
    )
}

@Composable
fun TimeText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color(0xFFE8F2FF).copy(alpha = 0.82f),
        fontSize = 14.sp,
        modifier = modifier
    )
}

@Composable
fun CityMeta(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.9f),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun HeroTemp(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = Color(0xFFF5FBFF),
        fontSize = 56.sp,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 1.em,
        letterSpacing = -0.06.em,
        modifier = modifier
    )
}

@Composable
fun StatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = WeatherTheme.frostedGlassStatPillBackground,
                shape = RoundedCornerShape(WeatherTheme.statPillCornerRadius)
            )
            .border(1.dp, WeatherTheme.frostedGlassStatPillBorder, RoundedCornerShape(WeatherTheme.statPillCornerRadius))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = Color(0xFFD8E9FF).copy(alpha = 0.72f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.08.em
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    WeatherCard(modifier = modifier.fillMaxWidth().padding(vertical = 32.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }
}

@Composable
fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    WeatherCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Retry", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun InfoHeading(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = Color(0xFFF0F7FF).copy(alpha = 0.92f),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.12.em,
        modifier = modifier
    )
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isLast: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.03f),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = if (isLast) 0.dp else 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color(0xFFE8F2FF).copy(alpha = 0.92f),
                fontSize = 15.sp
            )
            Text(
                text = value,
                color = Color(0xFFF5FAFF).copy(alpha = 0.92f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(start = 4.dp)
    )
}

@Composable
fun TableContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = WeatherTheme.frostedGlassTableContainerBackground,
                shape = RoundedCornerShape(WeatherTheme.tableContainerCornerRadius)
            )
            .border(1.dp, WeatherTheme.frostedGlassTableContainerBorder, RoundedCornerShape(WeatherTheme.tableContainerCornerRadius))
    ) {
        content()
    }
}

@Composable
fun DayCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = WeatherTheme.frostedGlassDayCardBackground,
                shape = RoundedCornerShape(WeatherTheme.dayCardCornerRadius)
            )
            .border(1.dp, WeatherTheme.frostedGlassDayCardBorder, RoundedCornerShape(WeatherTheme.dayCardCornerRadius))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun DayDate(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color(0xFFDFEEFF),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(bottom = 8.dp).fillMaxWidth()
    )
}

@Composable
fun DayTemp(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(bottom = 12.dp).fillMaxWidth()
    )
}

@Composable
fun DayDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isLast: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = Color(0xFFE8F2FF).copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                color = Color(0xFFE8F2FF).copy(alpha = 0.9f),
                fontSize = 13.sp
            )
        }
        if (!isLast) {
            Divider(
                color = Color.White.copy(alpha = 0.12f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 5.dp).fillMaxWidth()
            )
        }
    }
}
