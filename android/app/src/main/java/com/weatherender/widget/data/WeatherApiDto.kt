package com.weatherender.widget.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    val location: LocationDto,
    val current: CurrentDto,
    val forecast: ForecastDto,
    @SerialName("snow_state") val snowState: SnowStateDto,
    @SerialName("snow_forecast") val snowForecast: List<SnowForecastDto>,
    val attribution: AttributionDto,
)

@Serializable
data class LocationDto(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    @SerialName("tz_id") val tzId: String,
    @SerialName("localtime") val localTime: String,
)

@Serializable
data class ConditionDto(
    val text: String,
    val icon: String,
    val code: Int,
)

@Serializable
data class CurrentDto(
    @SerialName("temp_c") val tempC: Double,
    @SerialName("temp_f") val tempF: Double,
    val condition: ConditionDto,
    @SerialName("wind_kph") val windKph: Double,
    @SerialName("wind_mph") val windMph: Double,
    @SerialName("wind_dir") val windDir: String,
    @SerialName("pressure_mb") val pressureMb: Double,
    val humidity: Int,
    val cloud: Int,
    val uv: Double,
    @SerialName("gust_kph") val gustKph: Double? = null,
    @SerialName("gust_mph") val gustMph: Double? = null,
)

@Serializable
data class ForecastDto(
    @SerialName("forecastday") val forecastDay: List<ForecastDayDto>,
)

@Serializable
data class ForecastDayDto(
    val date: String,
    @SerialName("date_epoch") val dateEpoch: Long,
    val day: DayDto,
    val hour: List<HourDto>,
)

@Serializable
data class DayDto(
    @SerialName("avgtemp_c") val avgTempC: Double,
    @SerialName("avgtemp_f") val avgTempF: Double,
    @SerialName("maxtemp_c") val maxTempC: Double,
    @SerialName("mintemp_c") val minTempC: Double,
    @SerialName("maxwind_kph") val maxWindKph: Double,
    @SerialName("maxwind_mph") val maxWindMph: Double,
    @SerialName("totalprecip_mm") val totalPrecipMm: Double,
    @SerialName("daily_chance_of_rain") val dailyChanceOfRain: Int,
    @SerialName("daily_chance_of_snow") val dailyChanceOfSnow: Int,
    @SerialName("daily_will_it_snow") val dailyWillItSnow: Int,
    @SerialName("totalsnow_cm") val totalSnowCm: Double,
    val uv: Double,
    @SerialName("gust_kph") val gustKph: Double? = null,
    @SerialName("gust_mph") val gustMph: Double? = null,
    val condition: ConditionDto,
)

@Serializable
data class HourDto(
    val time: String,
    @SerialName("temp_c") val tempC: Double,
    @SerialName("temp_f") val tempF: Double,
    @SerialName("chance_of_rain") val chanceOfRain: Int,
    @SerialName("chance_of_snow") val chanceOfSnow: Int,
    val uv: Double,
    @SerialName("pressure_mb") val pressureMb: Double,
    val condition: ConditionDto,
)

@Serializable
data class SnowStateDto(
    val status: String,
)

@Serializable
data class SnowForecastDto(
    val date: String,
    @SerialName("snow_state") val snowState: SnowStateDto,
)

@Serializable
data class AttributionDto(
    val copyright: String,
    val author: String,
    val license: String,
    @SerialName("license_url") val licenseUrl: String,
    @SerialName("trademarks_url") val trademarksUrl: String,
)
