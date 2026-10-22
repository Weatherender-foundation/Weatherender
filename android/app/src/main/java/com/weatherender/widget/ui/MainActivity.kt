package com.weatherender.widget.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.weatherender.widget.data.JsonFileCache
import com.weatherender.widget.data.WeatherRepository

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        WeatherRepository(
            cache = JsonFileCache(this),
            apiClient = com.weatherender.widget.data.ApiClient
        )
    }

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(repository, this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTheme.WeatherAppTheme {
                MainScreen(viewModel = viewModel)
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
        return MainViewModel(repository, context) as T
    }
}
