# Implementation Plan: Android App with Main Screen and Widgets

**Branch**: `feature/android` | **Date**: 2026-10-18 | **Spec**: specs/001-android-weather-widgets/spec.md

**Input**: Feature specification from `/specs/001-android-weather-widgets/spec.md`

## Summary

Build a new Kotlin-only Android application (`./android`) with:
- A full-screen main app screen (single Compose screen, Material 3) in the web UI visual style: city search, current weather, 3-day forecast, condition-based gradients, semi-transparent cards with white borders, matching typography
- Two home screen widgets (Current Weather and 3-Day Forecast) using Jetpack Glance, static (no animations), same visual style
Both app screen and widgets fetch from the existing FastAPI `/api/v2/weather` endpoint, share the data layer (repository, cache, API, theme), and respect API rate limits. No changes to Python backend code.

## Technical Context

**Language/Version**: Kotlin 2.0+, JVM target 17

**Primary Dependencies**:
- Jetpack Compose (app screen UI, Material 3)
- Jetpack Glance (widgets UI framework, `androidx.glance:glance:1.1.0+`)
- Retrofit 2.9+ + OkHttp 4.12+ (networking)
- Kotlinx Serialization 1.6+ (JSON parsing with typed DTOs)
- Room 2.6+ (local caching of API responses)
- WorkManager 2.9+ (background refresh respecting rate limits)
- DataStore Preferences (widget/app configuration persistence)

**Storage**: Room database for cached weather responses + DataStore for widget configuration (city name, widget IDs, last update timestamps) and app configuration (city name, last update timestamp)

**Testing**: JUnit 5 + MockK (unit), Robolectric (widget/app rendering tests), Compose UI Testing (Glance widgets and Compose screen)

**Target Platform**: Android API 26+ (minSdk 26, targetSdk 35)

**Project Type**: Mobile app (main screen + widgets)

**Performance Goals**: Widget/app data load <5s on 4G/WiFi; automatic refresh every 30 min max; manual refresh on tap; offline display up to 24h

**Constraints**:
- API rate limit: 25 req/min → auto-refresh max every 30 min
- Widgets are static (Glance) - no animations, particle effects, or backdrop blur
- App screen is static layout - no cursor/touch-following tilt, parallax, or 3D rotation
- API base URL configured at build time via `local.properties` → `BuildConfig`

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
| VII. Simplicity | ✅ | Minimal dependencies, single Compose screen |

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
# Mobile + API structure (Option 3)
android/
├── app/
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/weatherender/widget/
│   │   │   │   ├── config/          # Widget/app configuration activity
│   │   │   │   ├── data/            # Repository, API, cache, models
│   │   │   │   │   ├── api/         # Retrofit service, typed DTOs
│   │   │   │   │   ├── cache/       # Room entities, DAO, database
│   │   │   │   │   ├── model/       # Domain models (mapped from DTOs)
│   │   │   │   │   └── repository/  # WeatherRepository
│   │   │   │   ├── ui/              # Glance widgets + Compose app screen
│   │   │   │   │   ├── current/     # CurrentWeatherWidget (Glance)
│   │   │   │   │   ├── forecast/    # ForecastWidget (Glance)
│   │   │   │   │   ├── app/         # MainAppScreen (Compose)
│   │   │   │   │   └── theme/       # Condition themes, gradients, colors (shared)
│   │   │   │   ├── util/            # Extensions, mappers, constants
│   │   │   │   └── worker/          # WorkManager refresh worker
│   │   │   └── res/
│   │   │       ├── xml/             # Widget info XML (appwidget-provider)
│   │   │       ├── values/          # Colors, dimensions, strings
│   │   │       └── layout/          # Configuration activity layout
│   │   ├── test/                    # Unit tests
│   │   └── androidTest/             # Instrumented tests
├── gradle/
│   └── libs.versions.toml           # Version catalog
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── local.properties.template        # Template for API base URL (not committed)
```

**Structure Decision**: Option 3 (Mobile + API) selected. The Android app is a standalone module at `./android` with its own Gradle build. It consumes the existing FastAPI backend. No iOS module.

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

### 2. Networking and Caching Libraries
**Decision**: **Retrofit + OkHttp + Kotlinx Serialization** for networking; **Room** for caching
**Rationale**:
- Retrofit is the de facto standard for Android REST clients
- Kotlinx Serialization with typed DTOs matching `WeatherResponseV2` structure (LocationDto, CurrentDto, ForecastDto, DayDto, SnowStateDto, SnowForecastDto, AttributionDto) - avoids `Map<String, Any>` deserialization issues
- Room provides structured, queryable caching with TTL support via DAO; better than DataStore for JSON blobs
- Cache schema: `CachedWeatherEntity(entity_id, city, jsonResponse, timestamp, etag?)`
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

## Complexity Tracking

> No Constitution Check violations - all decisions align with principles.

## Phase 0: Research (Completed Inline)

All technical ambiguities resolved above. Key findings:
- WeatherAPI returns 3 `forecastday` entries (today + 2 days) - confirmed in `main.py:93` and CLI usage
- `WeatherResponseV2` structure: typed DTOs for Location, Current, Forecast, Day, SnowState, SnowForecast, Attribution - use `@Serializable` data classes
- Web UI CSS provides exact gradient hex values for 4 conditions (sunny, rainy, cloudy, thunder)
- Card styling: `rgba(18, 27, 38, 0.34)` bg, `rgba(255,255,255,0.18)` border, 30px radius (main card); inner blocks use `rgba(255,255,255,0.06)` bg, `rgba(255,255,255,0.12)` border, 22px radius

## Phase 1: Design Artifacts (To Be Generated)

- `data-model.md`: Domain entities (WidgetConfig, AppConfig, CachedWeather, ConditionTheme, CurrentWeatherDisplay, ForecastDayDisplay)
- `contracts/`: API DTOs matching `WeatherResponseV2` structure; widget/app configuration intent schema
- `quickstart.md`: Steps to build, configure `local.properties`, run on emulator/device, add widgets, launch app, verify data
- `research.md`: This section documents decisions
