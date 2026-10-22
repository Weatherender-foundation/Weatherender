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
    @SerialName("wind_kph") val windKph: Double? = null,
    @SerialName("wind_mph") val windMph: Double? = null,
    @SerialName("wind_dir") val windDir: String? = null,
    @SerialName("pressure_mb") val pressureMb: Double? = null,
    val humidity: Int? = null,
    val cloud: Int? = null,
    val uv: Double? = null,
    @SerialName("gust_kph") val gustKph: Double? = null,
    @SerialName("gust_mph") val gustMph: Double? = null,
    @SerialName("windchill_c") val windchillC: Double? = null,
    @SerialName("windchill_f") val windchillF: Double? = null,
    @SerialName("heatindex_c") val heatindexC: Double? = null,
    @SerialName("heatindex_f") val heatindexF: Double? = null,
    @SerialName("dewpoint_c") val dewpointC: Double? = null,
    @SerialName("dewpoint_f") val dewpointF: Double? = null,
    @SerialName("vis_km") val visKm: Double? = null,
    @SerialName("vis_miles") val visMiles: Double? = null,
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
    @SerialName("avgtemp_c") val avgTempC: Double? = null,
    @SerialName("avgtemp_f") val avgTempF: Double? = null,
    @SerialName("maxtemp_c") val maxTempC: Double? = null,
    @SerialName("maxtemp_f") val maxTempF: Double? = null,
    @SerialName("mintemp_c") val minTempC: Double? = null,
    @SerialName("mintemp_f") val minTempF: Double? = null,
    @SerialName("maxwind_kph") val maxWindKph: Double? = null,
    @SerialName("maxwind_mph") val maxWindMph: Double? = null,
    @SerialName("totalprecip_mm") val totalPrecipMm: Double? = null,
    @SerialName("daily_chance_of_rain") val dailyChanceOfRain: Int? = null,
    @SerialName("daily_chance_of_snow") val dailyChanceOfSnow: Int? = null,
    @SerialName("daily_will_it_snow") val dailyWillItSnow: Int? = null,
    @SerialName("totalsnow_cm") val totalSnowCm: Double? = null,
    val uv: Double? = null,
    @SerialName("gust_kph") val gustKph: Double? = null,
    @SerialName("gust_mph") val gustMph: Double? = null,
    val condition: ConditionDto? = null,
)

@Serializable
data class HourDto(
    val time: String? = null,
    @SerialName("temp_c") val tempC: Double? = null,
    @SerialName("temp_f") val tempF: Double? = null,
    @SerialName("chance_of_rain") val chanceOfRain: Int? = null,
    @SerialName("chance_of_snow") val chanceOfSnow: Int? = null,
    val uv: Double? = null,
    @SerialName("pressure_mb") val pressureMb: Double? = null,
    val condition: ConditionDto? = null,
    @SerialName("wind_kph") val windKph: Double? = null,
    @SerialName("wind_mph") val windMph: Double? = null,
    @SerialName("wind_dir") val windDir: String? = null,
    val humidity: Int? = null,
    val cloud: Int? = null,
    @SerialName("feelslike_c") val feelslikeC: Double? = null,
    @SerialName("feelslike_f") val feelslikeF: Double? = null,
    @SerialName("windchill_c") val windchillC: Double? = null,
    @SerialName("windchill_f") val windchillF: Double? = null,
    @SerialName("heatindex_c") val heatindexC: Double? = null,
    @SerialName("heatindex_f") val heatindexF: Double? = null,
    @SerialName("dewpoint_c") val dewpointC: Double? = null,
    @SerialName("dewpoint_f") val dewpointF: Double? = null,
    @SerialName("vis_km") val visKm: Double? = null,
    @SerialName("vis_miles") val visMiles: Double? = null,
    @SerialName("gust_kph") val gustKph: Double? = null,
    @SerialName("gust_mph") val gustMph: Double? = null,
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
