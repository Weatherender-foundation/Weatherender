# Implementation Plan: Android App with Main Screen and Widgets

**Branch**: `feature/android` | **Date**: 2026-10-19 | **Spec**: specs/001-android-weather-widgets/spec.md

## Summary

Build a new Kotlin-only Android application (`./android`) with:
- A full-screen main app screen (single Compose screen, Material 3) in the web UI visual style: city search, current weather, 3-day forecast, 24h hourly table, condition-based gradients, semi-transparent cards with white borders, matching typography, atmospheric animated effects
- Two home screen widgets (Current Weather and 3-Day Forecast) using Jetpack Glance, static (no animations), same visual style
- Both app screen and widgets fetch from the existing FastAPI `/api/v2/weather` endpoint, share the data layer (repository, raw file cache, API client, theme), and respect API rate limits. No changes to Python backend code.

## Technical Context

**Language/Version**: Kotlin 2.0+, JVM target 17

**Primary Dependencies (APPROVED)**:
- Jetpack Compose BOM + Material 3 (app screen UI)
- `androidx.lifecycle:lifecycle-viewmodel-compose` (ViewModel)
- Jetpack Glance (widgets UI framework, `androidx.glance:glance:1.1.0+`)
- WorkManager 2.9+ (background refresh respecting rate limits)
- DataStore Preferences (widget/app configuration persistence)
- OkHttp 4.12+ (networking)
- Kotlinx Serialization 1.6+ (JSON parsing with typed DTOs)

**Explicitly FORBIDDEN**: Retrofit, Room, Hilt, Navigation libraries

**Storage**: Raw file cache (last API JSON per city) + DataStore for widget configuration (city name, widget IDs, last update timestamps) and app configuration (city name, last update timestamp, unit preference)

**Testing**: JUnit 5 + MockK (unit), Robolectric (widget/app rendering tests), Compose UI Testing (Glance widgets and Compose screen)

**Target Platform**: Android API 26+ (minSdk 26, targetSdk 35)

**Project Type**: Mobile app (main screen + widgets)

**Performance Goals**: Widget/app data load <5s on 4G/WiFi; automatic refresh every 30 min max; manual refresh on tap; offline display up to 24h

**Constraints**:
- API rate limit: 25 req/min → auto-refresh max every 30 min
- Widgets are static (Glance) - no animations, particle effects, or backdrop blur
- App screen is static layout - no cursor/touch-following tilt, parallax, or 3D rotation
- API base URL configured at build time via `local.properties` → `BuildConfig`
- Client performs ONLY allowed display computations: pressure mb→mmHg, 24h window selection, condition→theme mapping, gusts fallback (maxwind*1.2)

**Scale/Scope**: 2 widget types × multiple widget instances per user; single main app screen; single configuration activity shared by widgets

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Library-First | N/A | Feature is a mobile app, not a library |
| II. CLI Interface | N/A | No CLI component |
| III. Test-First | ✅ | Unit/widget/app tests will be written |
| IV. Integration Testing | ✅ | Widget and app integration tests planned |
| V. Observability | ✅ | Logging for network/cache/widget/app lifecycle |
| VI. Versioning | ✅ | Semantic versioning via Gradle |
| VII. Simplicity | ✅ | Minimal dependencies, single Compose screen, raw file cache |

## Project Structure

### Documentation (this feature)

```text
specs/001-android-weather-widgets/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
└── tasks.md             # Phase 2 output
```

### Source Code (repository root)

```text
# Mobile + API structure
android/
├── app/
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/weatherender/widget/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── current/     # CurrentWeatherWidget (Glance)
│   │   │   │   │   ├── forecast/    # ForecastWidget (Glance)
│   │   │   │   │   ├── app/         # MainAppScreen (Compose) + MainViewModel
│   │   │   │   │   ├── theme/       # ConditionTheme, CardStyle, Typography, Effects
│   │   │   │   │   └── config/      # ConfigScreen (Compose)
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/         # OkHttp client, typed DTOs
│   │   │   │   │   ├── cache/       # Raw file cache (JSON per city)
│   │   │   │   │   ├── config/      # DataStore preferences
│   │   │   │   │   ├── repository/  # WeatherRepository
│   │   │   │   │   └── worker/      # WeatherRefreshWorker
│   │   │   │   └── model/           # Client models (only if ≥2 files and different from DTOs)
│   │   │   └── res/
│   │   │       ├── xml/             # Widget info XML (appwidget-provider)
│   │   │       ├── values/          # Colors, dimensions, strings
│   │   │       └── drawable/        # previewImage for widgets
│   │   ├── test/                    # Unit tests
│   │   └── androidTest/             # Instrumented tests
├── gradle/
│   └── libs.versions.toml           # Version catalog
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── local.properties.template        # Template for API base URL (not committed)
```

**Structure Decision**: Flat 3-folder package (`ui/`, `data/`, `model/`) with max 4 nesting levels. No `di/` folder — dependency wiring in `Application` subclass.

## Technical Decisions (Resolved from Spec Ambiguities)

### 1. Jetpack Glance vs RemoteViews
**Decision**: **Jetpack Glance** (modern, Compose-based)
**Rationale**:
- Official modern widget framework since Android 12/13
- Declarative Compose-like API matches web UI component mental model
- Better theming support for condition-based gradients
- Easier to maintain visual parity with web UI (card styles, typography scale)
- RemoteViews is legacy, limited layout flexibility, harder to theme dynamically
- Glance supports `GlanceAppWidget` with `updatePeriodMillis` and `onUpdate` for refresh

### 2. Networking and Caching (No Retrofit, No Room)
**Decision**: **OkHttp + Kotlinx Serialization** for networking; **Raw file cache** for caching
**Rationale**:
- OkHttp is the foundation Retrofit is built on; direct usage avoids Retrofit dependency
- Kotlinx Serialization with typed DTOs matching `WeatherResponseV2` structure - avoids `Map<String, Any>` deserialization issues
- Raw file cache (one JSON file per city in `filesDir`) is simpler than Room for this use case: single key (city), single value (raw API response), no queries needed
- Cache schema: `filesDir/weather_cache/{city_normalized}.json` + metadata file for timestamp
- WorkManager handles background refresh with exponential backoff and rate-limit awareness

### 3. Refresh Mechanism
**Decision**: **WorkManager periodic (30 min) + Manual tap refresh via `actionRunCallback`**
**Rationale**:
- `GlanceAppWidget.updatePeriodMillis` minimum is 30 min (1800000ms) - matches rate limit
- WorkManager provides guaranteed execution, backoff, network constraints (`NetworkType.CONNECTED`)
- Manual refresh: tap widget → `actionRunCallback` → `WidgetRefreshScheduler.scheduleImmediateRefresh(city)` → `GlanceAppWidget.update`
- App screen: pull-to-refresh or tap refresh button → same WorkManager immediate work
- Stale data indicator: compare `System.currentTimeMillis() - cached.timestamp > 30 min`

### 4. Min/Target SDK
**Decision**: **minSdk 26 (Android 8.0), targetSdk 35 (Android 15)**
**Rationale**:
- Jetpack Glance requires API 26+
- Target SDK 35 ensures modern privacy/permission behaviors
- Covers 95%+ of active Android devices (per Google Play distribution)

### 5. API Base URL Configuration (No Hardcoding)
**Decision**: **Gradle `local.properties` → `BuildConfig` field**
**Implementation**:
- `local.properties` (gitignored): `api.base.url=https://YOUR_API_HOST`
- `build.gradle.kts`: `buildConfigField("String", "API_BASE_URL", "\"${property("api.base.url")}\"")`
- `local.properties.template` committed with placeholder: `api.base.url=https://YOUR_API_HOST`
- Developers copy template to `local.properties` and fill in; CI injects secret at build time

### 6. Dependency Wiring (No Hilt, No di/ folder)
**Decision**: **Manual wiring in `WeatherenderApplication` subclass**
**Rationale**:
- Single Application class creates OkHttp client, WeatherRepository, DataStore preferences, WorkManager scheduler
- ViewModels created via `viewModel()` Compose function (lifecycle-viewmodel-compose)
- No DI framework needed for this scope

### 7. Widget Configuration (Compose, Not XML)
**Decision**: **Compose-based configuration activity using `ComponentActivity.setContent()`**
**Rationale**:
- Spec requires Compose-only UI
- `android.appwidget.AppWidgetProvider` configuration activity can be a Compose Activity
- `previewImage` in widget XML for widget picker preview (no layout XML)

## Complexity Tracking

> No Constitution Check violations - all decisions align with principles.

## Phase 0: Research (Completed Inline)

All technical ambiguities resolved above. Key findings:
- WeatherAPI returns 3 `forecastday` entries (today + 2 days) - confirmed in `main.py:93` and CLI usage
- `WeatherResponseV2` structure: typed DTOs for Location, Current, Forecast, Day, SnowState, SnowForecast, Attribution - use `@Serializable` data classes
- Web UI CSS provides exact gradient hex values for 4 conditions (sunny, rainy, cloudy, thunder)
- Card styling: `rgba(18, 27, 38, 0.34)` bg, `rgba(255,255,255,0.18)` border, 30px radius (main card); inner blocks use `rgba(255,255,255,0.06)` bg, `rgba(255,255,255,0.12)` border, 22px radius
- Unit toggle labels from `units.js`: "°C / °F | km/h / mph" and "°F / °C | mph / km/h"
- Daily gusts fallback: `maxwind_kph * 1.2` (from `app.py`)

## Phase 1: Design Artifacts (To Be Generated)

- `data-model.md`: Domain entities (WidgetConfig, AppConfig, CachedWeather, ConditionTheme, CurrentWeatherDisplay, ForecastDayDisplay, HourlyEntry)
- `contracts/`: API DTOs matching `WeatherResponseV2` structure; widget/app configuration intent schema
- `quickstart.md`: Steps to build, configure `local.properties`, run on emulator/device, add widgets, launch app, verify data
- `research.md`: This section documents decisions

## Phase 2: Task Breakdown (To Be Generated)

Tasks grouped by user story to enable independent implementation and testing. See `tasks.md`.
