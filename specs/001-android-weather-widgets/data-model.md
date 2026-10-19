# Data Model: Android DTOs from `WeatherResponseV2`

Source: `src/weatherender/API/pydantic_schemas.py` (`WeatherResponseV2`) + `src/weatherender/WEB/app.py` (fields consumed by web UI).

## Top-Level Response

```kotlin
@Serializable
data class WeatherResponseDto(
    val location: LocationDto,
    val current: CurrentDto,
    val forecast: ForecastDto,
    val snow_state: SnowStateDto,
    val snow_forecast: List<SnowForecastDto>,
    val attribution: AttributionDto,
)
```

---

## LocationDto (from `data["location"]`)

| Field | Type | Used By Web | Notes |
|-------|------|-------------|-------|
| `name` | String | ✅ City display | |
| `region` | String | ✅ "City, Region" | |
| `country` | String | ✅ | |
| `lat` | Double | ✅ Elevation | |
| `lon` | Double | ✅ Elevation | |
| `tz_id` | String | ✅ Timezone for hourly | **Critical for 24h window** |
| `localtime_epoch` | Long | ❌ | Web uses `localtime` string |
| `localtime` | String | ✅ "2026-10-19 14:30" | **Critical for 24h window** |

```kotlin
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
```

---

## CurrentDto (from `data["current"]`)

| Field | Type | Used By Web | Notes |
|-------|------|-------------|-------|
| `temp_c` | Double | ✅ Hero temp, details | |
| `temp_f` | Double | ✅ Android: use directly for °F | |
| `condition` | ConditionDto | ✅ Text + icon | |
| `wind_kph` | Double | ✅ Daily wind | |
| `wind_mph` | Double | ✅ Android: use directly for mph | |
| `wind_dir` | String | ❌ | |
| `pressure_mb` | Double | ✅ Convert to mmHg (*0.750062) | |
| `pressure_in` | Double | ❌ | |
| `humidity` | Int | ✅ | |
| `cloud` | Int | ✅ Snow state | |
| `uv` | Double | ✅ Rounded to Int | |
| `gust_kph` | Double? | ✅ Fallback: maxwind_kph * 1.2 | May be missing |
| `gust_mph` | Double? | ✅ Android: use directly | |

```kotlin
@Serializable
data class CurrentDto(
    @SerialName("temp_c") val tempC: Double,
    @SerialName("temp_f") val tempF: Double,
    val condition: ConditionDto,
    @SerialName("wind_kph") val windKph: Double,
    @SerialName("wind_mph") val windMph: Double,
    @SerialName("pressure_mb") val pressureMb: Double,
    val humidity: Int,
    val cloud: Int,
    val uv: Double,
    @SerialName("gust_kph") val gustKph: Double?,
    @SerialName("gust_mph") val gustMph: Double?,
)
```

---

## ConditionDto

```kotlin
@Serializable
data class ConditionDto(
    val text: String,        // e.g., "Partly cloudy"
    val icon: String,        // "//cdn.weatherapi.com/weather/64x64/day/116.png"
    val code: Int,           // WeatherAPI condition code
)
```

---

## ForecastDto (from `data["forecast"]`)

```kotlin
@Serializable
data class ForecastDto(
    @SerialName("forecastday") val forecastDay: List<ForecastDayDto>,
)
```

---

## ForecastDayDto (3 days)

| Field | Type | Used By Web | Notes |
|-------|------|-------------|-------|
| `date` | String | ✅ "2026-10-19" | |
| `date_epoch` | Long | ❌ | |
| `day` | DayDto | ✅ All daily card data | |
| `hour` | List<HourDto> | ✅ 24h table source | 24 entries per day |

```kotlin
@Serializable
data class ForecastDayDto(
    val date: String,
    val day: DayDto,
    val hour: List<HourDto>,
)
```

---

## DayDto (from `forecastday[i].day`)

| Field | Type | Used By Web | Notes |
|-------|------|-------------|-------|
| `avgtemp_c` | Double | ✅ Daily temp | |
| `avgtemp_f` | Double | ✅ Android: use directly | |
| `maxtemp_c` | Double | ✅ Snow state | |
| `mintemp_c` | Double | ✅ Snow state | |
| `maxwind_kph` | Double | ✅ Daily wind, gust fallback | |
| `maxwind_mph` | Double | ✅ Android: use directly | |
| `totalprecip_mm` | Double | ✅ Precipitation mm | |
| `daily_chance_of_rain` | Int | ✅ Chance precip | |
| `daily_chance_of_snow` | Int | ✅ Chance precip | |
| `daily_will_it_snow` | Int | ✅ Snow state | |
| `totalsnow_cm` | Double | ✅ Snow state | |
| `uv` | Double | ✅ Max UV | |
| `gust_kph` | Double? | ✅ Gust fallback | May be missing |
| `gust_mph` | Double? | ✅ Android: use directly | |
| `condition` | ConditionDto | ✅ Daily condition text | |

```kotlin
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
    @SerialName("gust_kph") val gustKph: Double?,
    @SerialName("gust_mph") val gustMph: Double?,
    val condition: ConditionDto,
)
```

---

## HourDto (from `forecastday[i].hour[]`)

| Field | Type | Used By Web | Notes |
|-------|------|-------------|-------|
| `time` | String | ✅ "2026-10-19 14:00" | |
| `temp_c` | Double | ✅ Hourly temp | |
| `temp_f` | Double | ✅ Android: use directly | |
| `chance_of_rain` | Int | ✅ Precipitation % | |
| `chance_of_snow` | Int | ✅ Precipitation % | |
| `uv` | Double | ✅ UV index | |
| `pressure_mb` | Double | ✅ Convert to mmHg | |
| `condition` | ConditionDto | ❌ Not in table | |

```kotlin
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
```

---

## SnowStateDto

```kotlin
@Serializable
data class SnowStateDto(
    val status: String,  // e.g., "Good snow conditions", "No snow data"
)
```

---

## SnowForecastDto

```kotlin
@Serializable
data class SnowForecastDto(
    val date: String,           // "2026-10-19"
    val snow_state: SnowStateDto,
)
```

---

## AttributionDto

```kotlin
@Serializable
data class AttributionDto(
    val copyright: String,
    val author: String,
    val license: String,
    @SerialName("license_url") val licenseUrl: String,
    @SerialName("trademarks_url") val trademarksUrl: String,
)
```

---

## Computed/Derived Fields (Android-side)

| Field | Source | Formula |
|-------|--------|---------|
| `pressure_mmhg` | `pressure_mb` | `round(pressure_mb * 0.750062)` |
| `hourly_time_display` | `HourDto.time` | Split by space, take `[1]` → "14:00" |
| `daily_gust_kph` | `DayDto.gust_kph` ?: `DayDto.maxwind_kph * 1.2` | Round result |
| `daily_gust_mph` | `DayDto.gust_mph` ?: `DayDto.maxwind_mph * 1.2` | Round result |
| `precipitation_pct` | `HourDto` | `max(chance_of_rain, chance_of_snow)` |
| `condition_theme` | `CurrentDto.condition.text` | **FR-006 precedence**: thunder → cloudy → rainy → sunny (lowercase substring) |
| `snow_state` | `DayDto` + `CurrentDto` | Use `get_snow_state` algorithm (port from Python) |

---

## Unit Handling (Critical for Parity)

| Display | Metric Mode | Imperial Mode |
|---------|-------------|---------------|
| Temperature | `temp_c` + "°C" | `temp_f` + "°F" |
| Wind Speed | `wind_kph` + " km/h" | `wind_mph` + " mph" |
| Gust Speed | `gust_kph` + " km/h" | `gust_mph` + " mph" |
| Pressure | mmHg (converted) | mmHg (converted) |
| Precipitation | mm | mm |
| Toggle Label | `°C / °F \| km/h / mph` | `°F / °C \| mph / km/h` |

**Widgets always use metric** (per spec).
