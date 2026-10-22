package com.weatherender.widget.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherender.widget.data.AppPreferences
import com.weatherender.widget.data.WeatherRepository
import com.weatherender.widget.data.WeatherResponseDto
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
    private var currentResponse: WeatherResponseDto? = null

    init {
        loadUnitPreference()
    }

    private fun loadUnitPreference() {
        viewModelScope.launch {
            val metric = AppPreferences.isMetric(context)
            _isMetric.value = metric
        }
    }

    fun loadWeather(city: String, forceRefresh: Boolean = false) {
        currentCity = city
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getWeather(city, forceRefresh)
            result.onSuccess { response ->
                currentResponse = response
                _uiState.value = UiState.Success(response)
            }.onFailure { e ->
                _uiState.value = UiState.Error(e.message ?: "Unknown error", currentResponse)
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
            AppPreferences.saveUnitMetric(context, newValue)
        }
    }
}

sealed interface UiState {
    object Loading : UiState
    data class Success(val response: WeatherResponseDto) : UiState
    data class Error(val message: String, val cachedResponse: WeatherResponseDto?) : UiState
}
