package com.weatherender.widget.data

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import java.util.concurrent.TimeUnit

object WorkScheduler {
    private const val PERIODIC_WORK_NAME = "weatherender_periodic_refresh"

    fun schedulePeriodicRefresh(context: Context, city: String) {
        val request = PeriodicWorkRequestBuilder<WeatherRefreshWorker>(30, TimeUnit.MINUTES)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .addTag("weatherender")
            .setInputData(
                androidx.work.Data.Builder()
                    .putString(WeatherRefreshWorker.KEY_CITY, city)
                    .putBoolean(WeatherRefreshWorker.KEY_IS_MANUAL, false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    fun enqueueImmediateRefresh(context: Context, city: String, appWidgetId: Int? = null) {
        val request = OneTimeWorkRequestBuilder<WeatherRefreshWorker>()
            .addTag("weatherender_immediate")
            .setInputData(
                androidx.work.Data.Builder()
                    .putString(WeatherRefreshWorker.KEY_CITY, city)
                    .putBoolean(WeatherRefreshWorker.KEY_IS_MANUAL, true)
                    .putInt("app_widget_id", appWidgetId ?: -1)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}
