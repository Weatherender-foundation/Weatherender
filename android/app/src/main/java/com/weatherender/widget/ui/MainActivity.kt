package com.weatherender.widget.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.weatherender.widget.data.AppPreferences
import com.weatherender.widget.data.JsonFileCache
import com.weatherender.widget.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        WeatherRepository(
            cache = JsonFileCache(applicationContext),
            apiClient = com.weatherender.widget.data.ApiClient
        )
    }

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(repository, applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTheme.WeatherAppTheme {
                var city by remember { mutableStateOf<String?>(null) }
                LaunchedEffect(Unit) {
                    city = withContext(Dispatchers.IO) {
                        AppPreferences.getCity(applicationContext)
                    }
                }
                MainScreen(
                    viewModel = viewModel,
                    initialCity = city ?: "Moscow"
                )
            }
        }
    }
}

class MainViewModelFactory(
    private val repository: WeatherRepository,
    private val context: android.content.Context
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(repository, context.applicationContext) as T
    }
}
