package com.weatherender.widget.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent

fun normalizeConditionIconUrl(iconUrl: String): String {
    return if (iconUrl.startsWith("//")) "https:$iconUrl" else iconUrl
}

@Composable
fun WeatherConditionIcon(
    iconUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val normalizedUrl = normalizeConditionIconUrl(iconUrl)
    if (normalizedUrl.isBlank()) {
        WeatherConditionFallback(modifier)
        return
    }

    SubcomposeAsyncImage(
        model = normalizedUrl,
        contentDescription = contentDescription,
        modifier = modifier
    ) {
        if (painter.state is coil.compose.AsyncImagePainter.State.Success) {
            SubcomposeAsyncImageContent()
        } else {
            WeatherConditionFallback(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun WeatherConditionFallback(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🌤",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}
