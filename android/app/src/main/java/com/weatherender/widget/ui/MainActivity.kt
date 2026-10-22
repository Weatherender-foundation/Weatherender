package com.weatherender.widget.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    private var viewModel: MainViewModel? = null
    private var startupError: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val factory = MainViewModelFactory(repository, applicationContext)
            viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
            setContent {
                WeatherTheme.WeatherAppTheme {
                    var city by remember { mutableStateOf<String?>(null) }
                    LaunchedEffect(Unit) {
                        city = withContext(Dispatchers.IO) {
                            AppPreferences.getCity(applicationContext)
                        }
                    }
                    if (startupError != null) {
                        CrashFallbackScreen(startupError!!)
                    } else {
                        MainScreen(
                            viewModel = viewModel!!,
                            initialCity = city ?: "Moscow"
                        )
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("GlobalCrash", "Startup crash in MainActivity", e)
            startupError = "${e::class.java.simpleName}: ${e.message ?: "Unknown error"}"
            setContent {
                WeatherTheme.WeatherAppTheme {
                    CrashFallbackScreen(startupError!!)
                }
            }
        }
    }
}

@Composable
private fun CrashFallbackScreen(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Weatherender crashed on startup",
                color = androidx.compose.ui.graphics.Color.Red,
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = errorMessage,
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Please restart the app or reinstall if the problem persists.",
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
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
