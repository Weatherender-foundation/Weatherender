# Implementation Plan: Android Weather Widgets & App

**Branch**: `feature/android`
**Spec**: `specs/001-android-weather-widgets/spec.md`
**Data Model**: `specs/001-android-weather-widgets/data-model.md`
**Research**: `specs/001-android-weather-widgets/research.md`

---

## Architecture Overview

**Thin Client** — 3-folder package, max 4 nesting levels:
```
com.weatherender.widget/
├── ui/       # screens, components, effects, theme, widgets (current, forecast, app, theme, config)
├── data/     # OkHttp client, DTOs, repository, raw file cache, DataStore preferences, WorkManager worker
└── model/    # (empty — DTOs used directly)
```

**Approved Dependencies ONLY**:
- Compose BOM + Material 3, `lifecycle-viewmodel-compose`
- Glance (widgets), WorkManager, DataStore Preferences
- OkHttp, `kotlinx-serialization-json`
- Testing: **JUnit 4** + Robolectric

**Explicitly Forbidden**: Retrofit, Room, Hilt/Dagger, Navigation libraries.

---

## Phase Structure (Sequential, No Skipping)

| Phase | User Story | Priority | Description |
|-------|------------|----------|-------------|
| 1 | — | Setup | Gradle config, manifest, dependencies, project structure |
| 2 | — | Foundational | Data layer: OkHttp client, DTOs, DataStore, JsonFileCache, Repository, Worker |
| 3 | US1 | P1 | Current Weather Widget (Glance) |
| 4 | US2 | P1 | 3-Day Forecast Widget (Glance) |
| 5 | US5 | P2 | Widget Configuration (Compose activity) |
| 6 | US3 | P1 | Main App Screen (Compose) |
| 7 | US4 | P2 | Visual Style Parity (gradients, cards, typography) |
| **8** | **US6** | **P3 (Skippable)** | **Ambient Background Orbs & Canvas Atmospheric Effects** |
| **9** | **US7** | **P3 (Skippable)** | **24-Hour Forecast Table** |
| **10** | **US8** | **P3 (Skippable)** | **Unit Toggle with DataStore Persistence** |

> **Phases 8–10 are explicitly P3 and independently skippable.** They are placed LAST so core widget/app functionality delivers first.

---

## Phase 1: Setup (Gradle, Manifest, Project Structure)

**Files to create/modify**:
- `android/build.gradle.kts` — Compose BOM, Material 3, Glance, WorkManager, DataStore, OkHttp, kotlinx-serialization, JUnit 4 + Robolectric
- `android/app/build.gradle.kts` — Android plugin, Kotlin serialization, composeOptions
- `android/app/src/main/AndroidManifest.xml` — Permissions (INTERNET), widget providers (current, forecast), main activity, config activity
- `android/app/src/main/res/xml/current_widget_info.xml` — GlanceAppWidget metadata, previewImage
- `android/app/src/main/res/xml/forecast_widget_info.xml` — GlanceAppWidget metadata, previewImage
- `android/app/src/main/res/values/strings.xml` — App name, widget labels, error messages
- `android/app/src/main/res/values/colors.xml` — Theme colors (from web CSS gradients)
- `android/app/src/main/res/values/themes.xml` — Material 3 theme
- Package structure: `com.weatherender.widget.ui.{current,forecast,app,theme,config,effects}`, `com.weatherender.widget.data.{api,cache,config,repository,worker}`

**Dependencies**:
```kotlin
// build.gradle.kts (project)
plugins { id("org.jetbrains.kotlin.android") version "2.0.0" apply false }
plugins { id("com.android.application") version "8.5.0" apply false }
plugins { id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false }

// app/build.gradle.kts
dependencies {
    // Compose BOM + Material 3
    implementation(platform("androidx.compose:compose-bom:2024.08.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    // Glance (widgets)
    implementation("androidx.glance:glance:1.1.0")
    implementation("androidx.glance:glance-wearable:1.1.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // OkHttp + Serialization
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Testing: JUnit 4 + Robolectric
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.11")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
}
```

---

## Phase 2: Foundational Data Layer

### 2.1 API DTOs (`data/api/WeatherApiDto.kt`)
Typed `@Serializable` data classes matching `WeatherResponseV2`:
- `LocationDto`, `ConditionDto`, `CurrentDto`, `DayDto`, `HourDto`
- `ForecastDayDto`, `ForecastDto`, `SnowStateDto`, `SnowForecastDto`, `AttributionDto`
- `WeatherResponseDto` (root)

### 2.2 OkHttp Client (`data/api/ApiClient.kt`)
- Single `OkHttpClient` instance with timeout, logging interceptor (debug)
- `suspend fun fetchWeather(city: String): Result<WeatherResponseDto>`
- Base URL from `BuildConfig.API_BASE_URL` (set via `local.properties` → `buildConfigField`)
- Uses `kotlinx.serialization.json.Json` for parsing

### 2.3 DataStore Preferences (`data/config/Preferences.kt`)
- `WidgetPreferences` — per-widget city mapping: `city_<appWidgetId>` (String)
- `AppPreferences` — app city (String), unit preference (Boolean: metric=true), last update timestamps
- `DataStore<Preferences>` singleton via `preferencesDataStore`

### 2.4 JsonFileCache (`data/cache/JsonFileCache.kt`)
- `saveRawResponse(city: String, json: String)` — writes to `cacheDir/weather_<city>.json`
- `loadRawResponse(city: String): String?` — reads file
- `getLastModified(city: String): Long?` — file timestamp for stale indicator
- No Room, no TTL logic — raw JSON file per city (~70 KB)

### 2.5 Repository (`data/repository/WeatherRepository.kt`)
- `suspend fun getWeather(city: String, forceRefresh: Boolean = false): Result<WeatherResponseDto>`
- Logic: if `!forceRefresh && cache.exists(city)` → return cached (parse from JSON)
- Else → `ApiClient.fetchWeather(city)` → on success `cache.saveRawResponse(city, json)` → return parsed
- Error handling: network errors return cached data if available with `Result.failure`

### 2.6 WorkManager Worker (`data/worker/WeatherRefreshWorker.kt`)
- `CoroutineWorker` for periodic (30 min) and immediate (tap) refresh
- Input: `city`, `appWidgetId` (optional for widget-specific), `isManual`
- Calls `WeatherRepository.getWeather(city, forceRefresh = isManual)`
- On success: `GlanceAppWidget.update()` for affected widget IDs
- `PeriodicWorkRequest` (30 min, flex 5 min) enqueued on widget config save / app first launch

---

## Phase 3: US1 — Current Weather Widget (Glance)

### 3.1 `ui/current/CurrentWidget.kt`
- `GlanceAppWidget` implementation: `CurrentWidgetReceiver`
- `provideGlance()` → `CurrentWidgetContent(state)`
- State: `CurrentWidgetState(data: WeatherResponseDto?, error: String?, lastUpdated: Long?)`

### 3.2 `ui/current/CurrentWidgetContent.kt`
- Composable Glance layout: condition gradient background (FR-006)
- Hero temperature (`temp_c` + "°C"), condition text, UV, humidity, pressure (mmHg), snow status
- Loading skeleton / error state / stale indicator (timestamp)
- Tap action → `actionRunCallback` → `WeatherRefreshWorker` enqueue

### 3.3 `ui/current/CurrentWidgetTheme.kt`
- `ConditionThemeMapper.getTheme(conditionText: String): ConditionTheme`
- Returns gradient colors, text colors for 4 themes (sunny, rainy, cloudy, thunder)

---

## Phase 4: US2 — 3-Day Forecast Widget (Glance)

### 4.1 `ui/forecast/ForecastWidget.kt`
- `GlanceAppWidget`: `ForecastWidgetReceiver`
- State: `ForecastWidgetState(data: WeatherResponseDto?, error: String?, lastUpdated: Long?)`

### 4.2 `ui/forecast/ForecastWidgetContent.kt`
- 3 columns (or vertical stack for small widget), each day card
- Date (DD.MM.YYYY), avg temp (°C), precip mm, precip chance %, UV, wind km/h, gusts km/h, snow state
- Gusts: `day.gust_kph ?: (day.maxwind_kph * 1.2).roundToInt()` (FR-005)
- Same theme mapping, loading/error/stale states

---

## Phase 5: US5 — Widget Configuration (Compose Activity)

### 5.1 `ui/config/WidgetConfigActivity.kt`
- `ComponentActivity` — **100% Compose, no XML layout**
- `setContent { WidgetConfigScreen(onSave = { city, appWidgetId → ... }) }`
- Receives `appWidgetId` from intent extras

### 5.2 `ui/config/WidgetConfigScreen.kt`
- `TextField` for city input (1-100 chars, non-blank validation)
- Save button → validates → stores in DataStore `city_<appWidgetId>` → enqueues `WeatherRefreshWorker` → finishes with `RESULT_OK`
- Error display for invalid city / API error

### 5.3 Manifest Registration
```xml
<activity android:name=".ui.config.WidgetConfigActivity"
    android:exported="true"
    android:theme="@style/Theme.Weatherender.Config">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_CONFIGURE"/>
    </intent-filter>
</activity>
```

---

## Phase 6: US3 — Main App Screen (Compose)

### 6.1 `ui/app/MainActivity.kt`
- Single `ComponentActivity` with `MainScreenViewModel`
- `setContent { MainScreen(viewModel) }`
- Handles city search intent (from widget config or manual)

### 6.2 `ui/app/MainScreenViewModel.kt`
- `lifecycle-viewmodel-compose` — `viewModel()`
- State: `UiState(data, hourly, daily, loading, error, unitMetric, city)`
- `searchCity(city: String)` → calls repository → updates state
- `toggleUnits()` → flips `unitMetric` → saves to DataStore → triggers recomposition
- `refresh()` → forces repository refresh

### 6.3 `ui/app/MainScreen.kt`
- Scaffold: gradient background (condition theme), top app bar (city input + unit toggle)
- Content: Current weather hero card, Hourly table (placeholder for Phase 9), Daily forecast cards
- Loading / Error / Empty states
- `LaunchedEffect` for initial load

### 6.4 `ui/app/components/` — Shared Compose Components
- `ConditionGradient(theme)` — Box with gradient background
- `WeatherCard(theme)` — Semi-transparent card with white border, rounded corners
- `StatPill(label, value)` — Current weather stats
- `HeroTemp(temp, unit)` — Large temperature display
- `DayCard(day, unit)` — Daily forecast card
- `UnitToggleButton(isMetric, onClick)` — Label per `units.js`: metric=`°C / °F | km/h / mph`, imperial=`°F / °C | mph / km/h`

---

## Phase 7: US4 — Visual Style Parity (Static)

### 7.1 `ui/theme/ConditionTheme.kt`
- `ConditionThemeMapper.map(conditionText: String): ConditionTheme`
- Precedence: thunder (storm/thunder/lightning) → cloudy (cloud/overcast/fog/mist/haze/snow) → rainy (rain/drizzle/shower) → sunny (clear/sunny) → default sunny
- Lowercased substring match (per CSS `data-condition` rules)

### 7.2 `ui/theme/Gradients.kt`
- 4 gradient definitions matching `style.css` exactly:
  - Sunny: `linearGradient(135deg, #4facfe, #00f2fe)`
  - Rainy: `linearGradient(135deg, #0f2027, #203a43 35%, #2c5364)`
  - Cloudy: `linearGradient(135deg, #4b5d7d, #b5c7db 52%, #edf2ff)`
  - Thunder: `linearGradient(135deg, #0a1220, #1e2a39 32%, #2c3e50)`

### 7.3 `ui/theme/Typography.kt`
- Font sizes, weights matching web UI (clamp, letter-spacing)
- `eyebrow`, `h1`, `h2`, `h3`, `body`, `statLabel`, `statValue`, `heroTemp`

### 7.4 `ui/theme/CardStyles.kt`
- `weatherCardModifier(theme)` — background `rgba(18,27,38,0.34)`, border `rgba(255,255,255,0.18)`, radius 30dp, shadow, no backdrop blur
- `blockModifier(theme)` — background `rgba(255,255,255,0.06)`, border `rgba(255,255,255,0.12)`, radius 22dp
- `dayCardModifier(theme)` — background `rgba(255,255,255,0.06)`, border `rgba(255,255,255,0.1)`, radius 16dp

---

## Phase 8: US6 — Ambient Background Orbs & Canvas Atmospheric Effects (P3, Skippable)

**Location**: `ui/effects/`

### 8.1 `ui/effects/AmbientOrbs.kt`
- Two `Canvas` layers drawing radial-gradient circles (no blur API)
- Positions: top-right (-10rem, -8rem), bottom-left (-14rem, -8rem)
- Colors per theme (from CSS `body::before/::after`):
  - Sunny: `rgba(255,212,87,0.38)` / `rgba(142,224,255,0.44)`
  - Rainy: `rgba(95,176,255,0.35)` / `rgba(169,223,255,0.3)`
  - Cloudy: `rgba(255,255,255,0.45)` / `rgba(164,181,217,0.38)`
  - Thunder: `rgba(117,130,255,0.28)` / `rgba(140,168,255,0.2)`
- Animation: `rememberInfiniteTransition` → `animateFloat(0f, 1f, infiniteRepeatable(18s, RepeatMode.Reverse))`
- Drift: `translate3d(0,0,0) scale(1)` → `translate3d(26px,30px,0) scale(1.12)`

### 8.2 `ui/effects/HeroOrb.kt`
- `Canvas` inside weather-hero card: radial gradient circle
- Animation: `rememberInfiniteTransition` → 9s ease-in-out alternate
- `orbFloat`: `translate3d(0,0,0) scale(1)` → `translate3d(45px,20px,0) scale(1.1)`

### 8.3 `ui/effects/AtmosphericEffects.kt`
- **Sunny**: `SunGlow` (radial gradient, 0.4s opacity fade-in) + `Particles` (12s linear drift, bounded count)
- **Rainy**: `Clouds` (2 clouds, 18s floatCloud alternate) + `RainDrops` (3 streaks, 1.2s rainFall)
- **Cloudy**: `Clouds` (2 clouds, 18s floatCloud, opacity 0.9)
- **Thunder**: `Clouds` + `Lightning` (6s flash: 0→1→0.2→1→0) + `Stars` (4s twinkle alternate)
- All effects: `rememberInfiniteTransition`, paused via `LifecycleObserver` (ON_PAUSE/ON_RESUME)
- **System animator check**: `Settings.Global.getFloat(contentResolver, ANIMATOR_DURATION_SCALE, 1f) == 0f` → disable all animations
- **Particle cap**: max 50 particles (configurable), skip spawn if at cap
- **Unit tests**: `AtmosphericEffectsTest` — verify particle cap, disable logic, pause/resume

### 8.4 `ui/effects/FloatInAnimation.kt`
- `AnimatedVisibility` with `fadeIn + slideInVertically` (0.55s ease)
- Staggered delays: current=0s, hourly=0.08s, daily=0.12s

---

## Phase 9: US7 — 24-Hour Forecast Table (P3, Skippable)

### 9.1 `ui/app/components/HourlyForecastTable.kt`
- `LazyColumn` with sticky header (Time, Temp, Precip %, UV, Pressure)
- 24 rows from `hourlyForecast` (computed in ViewModel)
- Time: `HourDto.time` split by space → "HH:mm"
- Temp: `temp_c` or `temp_f` per unit toggle
- Precip %: `max(chance_of_rain, chance_of_snow)`
- UV: rounded `uv`
- Pressure: `round(pressure_mb * 0.750062)` + " mmHg" (never toggled)

### 9.2 `ui/app/MainScreenViewModel` additions
- `hourlyForecast: List<HourlyRow>` — computed from `WeatherResponseDto`:
  - Parse `location.localtime` → current hour string "YYYY-MM-DD HH:00"
  - Flatten `forecast.forecastday[*].hour` → filter `hour.time >= currentHourStr` → take 24
  - Map to `HourlyRow(timeDisplay, tempC, tempF, precipPct, uv, pressureMmHg)`

---

## Phase 10: US8 — Unit Toggle with DataStore Persistence (P3, Skippable)

### 10.1 `data/config/Preferences.kt` additions
- `APP_UNIT_METRIC_KEY = "app_unit_metric"` (Boolean, default true)
- `AppPreferences.isMetric: Boolean` / `setIsMetric(Boolean)`

### 10.2 `ui/app/components/UnitToggleButton.kt`
- Button with exact label strings from web `units.js`:
  - Metric: `"°C / °F | km/h / mph"`
  - Imperial: `"°F / °C | mph / km/h"`
- `onClick` → `viewModel.toggleUnits()`

### 10.3 `ui/app/MainScreenViewModel` additions
- `unitMetric: MutableStateFlow<Boolean>` — loaded from DataStore on init
- `toggleUnits()` → `unitMetric.update { !it }` → `preferences.setIsMetric(!it)`
- All display logic branches on `unitMetric.value`:
  - Temp: `if (metric) temp_c else temp_f`
  - Wind: `if (metric) wind_kph else wind_mph`
  - Gusts: `if (metric) gust_kph else gust_mph`
  - Pressure: always mmHg
  - Precipitation: always mm

### 10.4 Widget Isolation
- Widgets **always** use metric (FR-023) — no unit toggle in widget code
- Widget DataStore keys separate from app (`city_<appWidgetId>` vs `app_city`)

---

## Cross-Cutting Concerns

### Error Handling
- All API calls wrapped in `Result` — network errors, 4xx, 5xx
- UI shows error message + retry button
- Cached data shown with stale timestamp if available

### Offline Support
- `JsonFileCache` provides last raw JSON
- Repository returns cached data with `isStale = true` flag
- UI displays "Updated HH:mm (offline)" indicator

### Rate Limiting
- WorkManager periodic: 30 min minimum interval
- Manual refresh (tap) allowed but debounced (5 sec cooldown per widget/app)
- API returns 429 → show error, keep cached data

---

## Testing Strategy

| Layer | Framework | Scope |
|-------|-----------|-------|
| Unit (DTOs, mappers, cache, prefs) | JUnit 4 | Pure Kotlin logic, no Android |
| ViewModel / Repository | JUnit 4 + Robolectric | State transitions, cache/API logic |
| Glance Widgets | JUnit 4 + Robolectric | `provideGlance()` output, theme mapping |
| Compose UI | Compose Test + Robolectric | Screen rendering, interactions |
| Atmospheric Effects | JUnit 4 | Particle cap, animator scale=0 disable, pause/resume |

**Run command**:
```bash
./gradlew :android:app:testDebugUnitTest
```

---

## Deliverables Checklist

- [ ] `plan.md` — This document
- [ ] `tasks.md` — Step-by-step task list with IDs (T001–T060+)
- [ ] `research.md` — Web visual effects, logic parity (done)
- [ ] `data-model.md` — DTO definitions from `WeatherResponseV2` (done)
- [ ] `quickstart.md` — Build setup, `local.properties`, manual visual parity checklist

---

## Gate Approvals Required

1. **Spec Approval** — `spec.md` ✅ (already approved)
2. **Plan Approval** — This `plan.md` + `tasks.md` → **approved**

No implementation begins until **both gates pass**.
