package com.weatherender.widget.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WeatherWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val city = inputData.getString(KEY_CITY) ?: return Result.failure()
        val isManual = inputData.getBoolean(KEY_IS_MANUAL, false)

        val cache = JsonFileCache(applicationContext)
        val repo = WeatherRepository(cache)

        val result = repo.getWeather(city, forceRefresh = isManual)

        return if (result.isSuccess) Result.success() else if (isManual) Result.failure() else Result.retry()
    }

    companion object {
        const val KEY_CITY = "city"
        const val KEY_APP_WIDGET_ID = "app_widget_id"
        const val KEY_IS_MANUAL = "is_manual"
    }
}
