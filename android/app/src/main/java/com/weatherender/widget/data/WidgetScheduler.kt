package com.weatherender.widget.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import java.util.concurrent.TimeUnit

object WidgetScheduler {
    private const val PERIODIC_WORK_NAME = "weatherender_widget_periodic_refresh"
    private const val UPDATE_INTERVAL_MINUTES = 15L

    fun schedulePeriodicRefresh(context: Context, city: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<WeatherWorker>(UPDATE_INTERVAL_MINUTES, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag("weatherender_widget")
            .setInputData(
                androidx.work.Data.Builder()
                    .putString(WeatherWorker.KEY_CITY, city)
                    .putBoolean(WeatherWorker.KEY_IS_MANUAL, false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun enqueueImmediateRefresh(context: Context, city: String, appWidgetId: Int? = null) {
        val request = OneTimeWorkRequestBuilder<WeatherWorker>()
            .addTag("weatherender_widget_immediate")
            .setInputData(
                androidx.work.Data.Builder()
                    .putString(WeatherWorker.KEY_CITY, city)
                    .putBoolean(WeatherWorker.KEY_IS_MANUAL, true)
                    .putInt(WeatherWorker.KEY_APP_WIDGET_ID, appWidgetId ?: -1)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    fun cancelPeriodicRefresh(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME)
    }
}
