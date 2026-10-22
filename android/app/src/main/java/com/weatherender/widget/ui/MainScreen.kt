package com.weatherender.widget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weatherender.widget.data.WeatherResponseDto
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Spacer

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    initialCity: String = "Moscow"
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isMetric by viewModel.isMetric.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val currentState = uiState
    val (theme, backgroundBrush) = when (currentState) {
        is UiState.Success -> {
            val t = WeatherTheme.resolveTheme(currentState.response.current.condition.text)
            val b = WeatherTheme.getBackgroundBrush(t)
            t to b
        }
        else -> {
            val t = WeatherConditionTheme.SUNNY
            val b = WeatherTheme.darkBackgroundBrush
            t to b
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                UnitToggle(
                    isMetric = isMetric,
                    onToggle = { viewModel.toggleUnits() },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )

                when (currentState) {
                    is UiState.Loading -> {
                        LoadingIndicator(modifier = Modifier.padding(16.dp).fillMaxWidth())
                    }
                    is UiState.Error -> {
                        ErrorCard(
                            message = currentState.message,
                            onRetry = { viewModel.refresh() },
                            modifier = Modifier.padding(16.dp).fillMaxWidth()
                        )
                        currentState.cachedResponse?.let { cached ->
                            renderWeatherContent(cached, isMetric, theme)
                        }
                    }
                    is UiState.Success -> {
                        renderWeatherContent(currentState.response, isMetric, theme)
                    }
                }
            }
        }
    }
}

@Composable
private fun renderWeatherContent(
    response: WeatherResponseDto,
    isMetric: Boolean,
    theme: WeatherConditionTheme
) {
    val current = response.current
    val location = response.location
    val forecastHours = response.forecast.forecastDay.flatMap { it.hour }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CurrentWeatherCard(
            current = current,
            locationName = "${location.name}, ${location.country}",
            localTime = location.localTime,
            isMetric = isMetric,
            theme = theme
        )

        HourlyForecastTable(
            forecastHours = forecastHours,
            location = location,
            isMetric = isMetric
        )

        Spacer(modifier = Modifier.padding(32.dp))
    }
}
