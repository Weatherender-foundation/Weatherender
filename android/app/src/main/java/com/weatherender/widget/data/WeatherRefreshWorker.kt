package com.weatherender.widget.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WeatherRefreshWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val city = inputData.getString(KEY_CITY) ?: return Result.failure()
        val repo = WeatherRepository(JsonFileCache(applicationContext))
        val forceRefresh = inputData.getBoolean(KEY_IS_MANUAL, false)

        val result = repo.getWeather(city, forceRefresh = forceRefresh)
        return if (result.isSuccess) Result.success() else Result.retry()
    }

    companion object {
        const val KEY_CITY = "city"
        const val KEY_IS_MANUAL = "is_manual"
    }
}
