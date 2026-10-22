package com.weatherender.widget.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherender.widget.data.AppPreferences
import com.weatherender.widget.data.WeatherRepository
import com.weatherender.widget.data.WeatherResponseDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: WeatherRepository,
    private val context: android.content.Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _isMetric = MutableStateFlow(true)
    val isMetric: StateFlow<Boolean> = _isMetric

    private var currentCity: String? = null
    init {
        loadUnitPreference()
    }

    private fun loadUnitPreference() {
        viewModelScope.launch {
            try {
                val metric = AppPreferences.isMetric(context)
                _isMetric.value = metric
            } catch (e: Throwable) {
                _isMetric.value = true
            }
        }
    }

    fun loadWeather(city: String, forceRefresh: Boolean = false) {
        currentCity = city
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val data = repository.getWeather(city, forceRefresh).getOrThrow()
                _uiState.value = UiState.Success(data)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                Log.e("WeatherApp", "Fetch failed", e)
                val details = "${e::class.java.simpleName}:${e.localizedMessage ?: e.message ?: "No details"}"
                _uiState.value = UiState.Error(details)
            }
        }
    }

    fun refresh() {
        currentCity?.let { loadWeather(it, forceRefresh = true) }
    }

    fun toggleUnits() {
        val newValue = !_isMetric.value
        _isMetric.value = newValue
        viewModelScope.launch {
            try {
                AppPreferences.saveUnitMetric(context, newValue)
            } catch (e: Throwable) {
                _isMetric.value = !newValue
            }
        }
    }
}

sealed interface UiState {
    object Loading : UiState
    data class Success(val response: WeatherResponseDto) : UiState
    data class Error(val message: String, val cachedResponse: WeatherResponseDto? = null) : UiState
}
