package com.weatherender.widget.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherender.widget.R
import com.weatherender.widget.data.WorkScheduler
import com.weatherender.widget.data.WidgetPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class WidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the widget ID from the intent
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        // If no valid widget ID, finish with canceled result
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        setContent {
            WidgetConfigScreen(
                appWidgetId = appWidgetId,
                onSave = { city ->
                    saveCityAndFinish(city)
                },
                onCancel = {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            )
        }
    }

    private fun saveCityAndFinish(city: String) {
        // Save city to DataStore and enqueue refresh on IO thread
        val context = applicationContext
        val scope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO)
        scope.launch {
            // Save city to DataStore using WidgetPreferences
            withContext(Dispatchers.IO) {
                WidgetPreferences.saveCity(context, appWidgetId, city)
            }
            
            // Enqueue immediate refresh
            WorkScheduler.enqueueImmediateRefresh(context, city, appWidgetId)
            
            // Return success result with the widget ID
            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfigScreen(
    appWidgetId: Int,
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var cityText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val theme = WeatherConditionTheme.SUNNY
    val textColor = Color.White
    val backgroundBrush = WeatherTheme.getBackgroundBrush(theme)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = context.getString(R.string.widget_configure_city),
                    fontSize = 24.sp,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(24.dp))

                // City input field
                OutlinedTextField(
                    value = cityText,
                    onValueChange = { newText ->
                        cityText = newText.take(100) // Limit to 100 chars
                        if (errorText != null) {
                            errorText = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    label = { Text(text = context.getString(R.string.widget_city_hint), color = textColor.copy(alpha = 0.6f)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = VisualTransformation.None,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = textColor,
                        unfocusedBorderColor = textColor.copy(alpha = 0.3f),
                        focusedLabelColor = textColor,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        cursorColor = textColor,
                        focusedPlaceholderColor = textColor.copy(alpha = 0.4f),
                        unfocusedPlaceholderColor = textColor.copy(alpha = 0.4f)
                    )
                )

                // Error message
                errorText?.let { error ->
                    Text(
                        text = error,
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 8.dp)
                    )
                }

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))

                // Buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Save button
                    Button(
                        onClick = {
                            val trimmed = cityText.trim()
                            if (validateCity(trimmed)) {
                                isSaving = true
                                onSave(trimmed)
                            } else {
                                errorText = if (trimmed.isBlank()) {
                                    context.getString(R.string.error_city_required)
                                } else {
                                    context.getString(R.string.error_city_too_long)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = textColor,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = if (isSaving) "Saving…" else context.getString(R.string.widget_save),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Cancel button
                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    ) {
                        Text(
                            text = context.getString(R.string.widget_cancel),
                            fontSize = 16.sp,
                            color = textColor.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

internal fun validateCity(city: String): Boolean {
    return city.isNotBlank() && city.length <= 100
}