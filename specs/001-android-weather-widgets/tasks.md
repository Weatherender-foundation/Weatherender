# Tasks: Android App with Main Screen and Widgets

**Input**: Design documents from `/specs/001-android-weather-widgets/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are included for critical logic (repository, mappers, theme mapping). Widget/app rendering tests use Robolectric/Glance/Compose testing.

**Organization**: Tasks grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4, US5)
- Include exact file paths in descriptions

## Path Conventions

- **Mobile (Android)**: `android/app/src/main/java/com/weatherender/widget/...`
- **Resources**: `android/app/src/main/res/...`
- **Tests**: `android/app/src/test/...` (unit), `android/app/src/androidTest/...` (instrumented)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic Android module structure

- [ ] T001 Create Android module structure at `android/` with Gradle Kotlin DSL
- [ ] T002 Configure `settings.gradle.kts` to include `:app` module
- [ ] T003 [P] Setup version catalog (`gradle/libs.versions.toml`) with all dependencies
- [ ] T004 Configure `build.gradle.kts` (root) with plugins: `com.android.application`, `org.jetbrains.kotlin.android`, `kotlinx-serialization`
- [ ] T005 Configure `app/build.gradle.kts`: minSdk 26, targetSdk 35, compileSdk 35, Java 17, buildConfigField for API_BASE_URL from local.properties
- [ ] T006 [P] Create `local.properties.template` with `api.base.url=https://YOUR_API_HOST`
- [ ] T007 [P] Setup `AndroidManifest.xml` with widget providers, main activity, configuration activity, internet permission, WorkManager
- [ ] T008 [P] Configure linting (ktlint) and formatting (spotless) in root `build.gradle.kts`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

⚠️ **CRITICAL**: No user story work can begin until this phase is complete

### Data Layer - API & Models

- [ ] T009 [P] Create typed API DTOs in `android/app/src/main/java/com/weatherender/widget/data/api/WeatherApiDto.kt` matching `WeatherResponseV2` structure: `LocationDto`, `CurrentDto`, `ConditionDto`, `ForecastDto`, `ForecastDayDto`, `DayDto`, `SnowStateDto`, `SnowForecastDto`, `AttributionDto`, `WeatherResponseDto` (all `@Serializable` data classes)
- [ ] T010 [P] Create domain models in `android/app/src/main/java/com/weatherender/widget/data/model/WeatherModels.kt` (CurrentWeather, ForecastDay, SnowState, ConditionTheme) with only fields needed by widgets/app
- [ ] T011 Create Retrofit service in `android/app/src/main/java/com/weatherender/widget/data/api/WeatherApiService.kt` with `@GET("/api/v2/weather") suspend fun getWeather(@Query("city") city: String): WeatherResponseDto`
- [ ] T012 Create OkHttp client with logging interceptor + 10s timeout in `android/app/src/main/java/com/weatherender/widget/data/api/ApiClient.kt`

### Data Layer - Caching (Room)

- [ ] T013 [P] Create Room entity `CachedWeatherEntity` in `android/app/src/main/java/com/weatherender/widget/data/cache/CachedWeatherEntity.kt` (id, city, jsonResponse, timestamp, etag?)
- [ ] T014 [P] Create DAO `WeatherCacheDao` in `android/app/src/main/java/com/weatherender/widget/data/cache/WeatherCacheDao.kt` with insert, getByCity, deleteOlderThan(24h)
- [ ] T015 Create Room database `WeatherDatabase` in `android/app/src/main/java/com/weatherender/widget/data/cache/WeatherDatabase.kt` (abstract class, singleton via `Room.databaseBuilder`)

### Data Layer - Repository

- [ ] T016 Create `WeatherRepository` in `android/app/src/main/java/com/weatherender/widget/data/repository/WeatherRepository.kt` with `suspend fun getWeather(city: String): Result<WeatherDomain>` implementing: check cache → if fresh (<30 min) return cached → else fetch API → on success save to cache → return; on error return cached if exists with stale flag

### Configuration Persistence (DataStore)

- [ ] T017 [P] Create DataStore preferences `WidgetPreferences` in `android/app/src/main/java/com/weatherender/widget/data/config/WidgetPreferences.kt` with keys: cityName, widgetIds (Set<Long>), lastUpdateTimestamp
- [ ] T018 [P] Create DataStore preferences `AppPreferences` in `android/app/src/main/java/com/weatherender/widget/data/config/AppPreferences.kt` with keys: cityName, lastUpdateTimestamp

### Theme & Color System (from Web UI CSS)

- [ ] T019 [P] Create `ConditionTheme` enum/object in `android/app/src/main/java/com/weatherender/widget/ui/theme/ConditionTheme.kt` with 4 themes mapping condition text → gradients:
  - SUNNY: `#4facfe` → `#00f2fe` (web: `linear-gradient(135deg, #4facfe, #00f2fe)`)
  - RAINY: `#0f2027` → `#2c5364` (web: `linear-gradient(135deg, #0f2027, #203a43 35%, #2c5364)`)
  - CLOUDY: `#4b5d7d` → `#edf2ff` (web: `linear-gradient(135deg, #4b5d7d, #b5c7db 52%, #edf2ff)`)
  - THUNDER: `#0a1220` → `#2c3e50` (web: `linear-gradient(135deg, #0a1220, #1e2a39 32%, #2c3e50)`)
- [ ] T020 [P] Create card style constants in `android/app/src/main/java/com/weatherender/widget/ui/theme/WidgetCardStyle.kt`: mainCardBg = `Color(0x56121B26)` (rgba(18,27,38,0.34)), mainCardBorder = `Color(0x2EFFFFFF)` (rgba(255,255,255,0.18)), mainCardRadius = 30.dp; innerCardBg = `Color(0x0FFFFFFF)` (rgba(255,255,255,0.06)), innerCardBorder = `Color(0x1FFFFFFF)` (rgba(255,255,255,0.12)), innerCardRadius = 22.dp; statPillBg = `Color(0x2607111C)` (rgba(7,17,28,0.15)), statPillBorder = `Color(0x14FFFFFF)` (rgba(255,255,255,0.08)), statPillRadius = 14.dp
- [ ] T021 [P] Create typography scale in `android/app/src/main/java/com/weatherender/widget/ui/theme/WidgetTypography.kt` matching web UI: heroTemp = 48sp/700, cityName = 18sp/700, conditionText = 13sp/600, statLabel = 11sp/uppercase, statValue = 16sp, dayDate = 16sp/700, dayTemp = 22sp/700, dayDetail = 13sp

### Utility & Mapping

- [ ] T022 Create `WeatherMapper` in `android/app/src/main/java/com/weatherender/widget/util/WeatherMapper.kt` mapping `WeatherResponseDto` → domain models (extract temp_c, condition.text, uv, humidity, pressure_mb→mmHg, snow_state, forecastday[0..2] with date, avgtemp_c, totalprecip_mm, daily_chance_of_rain, uv, maxwind_kph, totalsnow_cm, daily_will_it_snow)
- [ ] T023 [P] Create condition text → `ConditionTheme` mapper in `android/app/src/main/java/com/weatherender/widget/util/ConditionThemeMapper.kt` (sunny/clear→SUNNY, rain/drizzle/shower→RAINY, cloud/overcast/fog/mist/haze/snow→CLOUDY, storm/thunder/lightning→THUNDER)
- [ ] T024 [P] Create date formatter `DateFormatter` in `android/app/src/main/java/com/weatherender/widget/util/DateFormatter.kt` for DD.MM.YYYY from YYYY-MM-DD

### Background Refresh (WorkManager)

- [ ] T025 Create `WeatherRefreshWorker` in `android/app/src/main/java/com/weatherender/widget/worker/WeatherRefreshWorker.kt` extending `CoroutineWorker`: fetch for all configured cities via repository, update Glance widgets via `GlanceAppWidget.updateAll(context, widgetClass)`, update app via callback/broadcast
- [ ] T026 [P] Create `WidgetRefreshScheduler` in `android/app/src/main/java/com/weatherender/widget/worker/WidgetRefreshScheduler.kt` to enqueue periodic work (30 min, `ExistingPeriodicWorkPolicy.KEEP`) and immediate work on manual refresh

### Configuration Activity

- [ ] T027 Create configuration activity layout `res/layout/activity_widget_config.xml` with EditText (city name, 1-100 chars), Save button, error TextView
- [ ] T028 Create `WidgetConfigActivity` in `android/app/src/main/java/com/weatherender/widget/config/WidgetConfigActivity.kt`: validate input (non-blank, 1-100 chars), save to DataStore, return `RESULT_OK` with widget ID, update widget via `GlanceAppWidget.update`

---

## Phase 3: User Story 1 - Current Weather Widget (Priority: P1) 🎯 MVP

**Goal**: Home screen widget displaying current weather (temperature, condition, UV, humidity, pressure, snow status) with condition-based theming

**Independent Test**: Add widget to home screen → configure city → verify current weather data displays from API within 5s; test offline shows cached data with stale indicator

### Tests for User Story 1

- [ ] T029 [P] [US1] Unit test `WeatherRepository` cache-fresh/expired/error paths in `android/app/src/test/.../data/repository/WeatherRepositoryTest.kt`
- [ ] T030 [P] [US1] Unit test `WeatherMapper` extracts correct fields in `android/app/src/test/.../util/WeatherMapperTest.kt`
- [ ] T031 [P] [US1] Unit test `ConditionThemeMapper` all condition variations in `android/app/src/test/.../util/ConditionThemeMapperTest.kt`

### Implementation for User Story 1

- [ ] T032 [P] [US1] Create Glance widget `CurrentWeatherWidget` in `android/app/src/main/java/com/weatherender/widget/ui/current/CurrentWeatherWidget.kt` extending `GlanceAppWidget`: `provideGlance` builds layout with condition gradient background, main card (hero temp, city, condition pill), 3 stat pills (UV, Humidity, Pressure), snow status row, last updated timestamp + stale indicator
- [ ] T033 [US1] Register `CurrentWeatherWidget` in `AndroidManifest.xml` with `<receiver android:name=".ui.current.CurrentWeatherWidget" android:exported="true">` + `<intent-filter><action android:name="android.appwidget.action.APPWIDGET_UPDATE"/></intent-filter>` + `<meta-data android:name="android.appwidget.provider" android:resource="@xml/current_widget_info"/>`
- [ ] T034 [P] [US1] Create widget info XML `res/xml/current_widget_info.xml` with `minWidth="250dp"`, `minHeight="200dp"`, `updatePeriodMillis="1800000"`, `configure="com.weatherender.widget.config.WidgetConfigActivity"`, `previewLayout="@layout/current_widget_preview"`
- [ ] T035 [P] [US1] Create preview layout `res/layout/current_widget_preview.xml` (static preview for widget picker)
- [ ] T036 [US1] Implement tap-to-refresh: `actionRunCallback` → `WidgetRefreshScheduler.scheduleImmediateRefresh(city)` → `GlanceAppWidget.update`
- [ ] T037 [US1] Implement error state: show "Failed to load" + retry button (triggers refresh) + last cached data if available

---

## Phase 4: User Story 2 - 3-Day Forecast Widget (Priority: P1)

**Goal**: Separate home screen widget showing 3-day forecast with daily average temp, precipitation, UV, wind, snow state

**Independent Test**: Add forecast widget → configure city → verify 3 days display with correct date format (DD.MM.YYYY), units (Celsius, km/h, mm), condition theming

### Tests for User Story 2

- [ ] T038 [P] [US2] Unit test `WeatherMapper` forecast day mapping (3 days) in `android/app/src/test/.../util/WeatherMapperForecastTest.kt`
- [ ] T039 [P] [US2] Unit test forecast date formatting in `android/app/src/test/.../util/DateFormatterTest.kt`

### Implementation for User Story 2

- [ ] T040 [P] [US2] Create Glance widget `ForecastWidget` in `android/app/src/main/java/com/weatherender/widget/ui/forecast/ForecastWidget.kt` extending `GlanceAppWidget`: layout with condition gradient background, horizontal scroll or column of 3 day cards (date, avg temp, precip mm, precip chance %, UV, wind km/h, gusts km/h, snow state)
- [ ] T041 [US2] Register `ForecastWidget` in `AndroidManifest.xml` with separate receiver, intent-filter, meta-data pointing to `forecast_widget_info.xml`
- [ ] T042 [P] [US2] Create widget info XML `res/xml/forecast_widget_info.xml` with `minWidth="400dp"`, `minHeight="200dp"`, `updatePeriodMillis="1800000"`, `configure="com.weatherender.widget.config.WidgetConfigActivity"`
- [ ] T043 [P] [US2] Create preview layout `res/layout/forecast_widget_preview.xml`
- [ ] T044 [US2] Share configuration activity logic (T028) - both widgets use same city config per widget instance
- [ ] T045 [US2] Implement tap-to-refresh (`actionRunCallback`) and error state same as Current widget

---

## Phase 5: User Story 3 - Main App Screen (Priority: P1)

**Goal**: Full-screen Compose app screen with city search, current weather, 3-day forecast, condition-based theming, matching web UI visual style

**Independent Test**: Launch app → enter city → verify current weather and 3-day forecast display with web UI visual parity; test invalid city shows error; test offline shows cached data with stale indicator

### Tests for User Story 3

- [ ] T046 [P] [US3] Unit test `MainViewModel` city search, loading, error, offline states in `android/app/src/test/.../ui/app/MainViewModelTest.kt`
- [ ] T047 [P] [US3] Unit test app screen Compose rendering with mocked data in `android/app/src/test/.../ui/app/MainAppScreenTest.kt`

### Implementation for User Story 3

- [ ] T048 [US3] Create `MainViewModel` in `android/app/src/main/java/com/weatherender/widget/ui/app/MainViewModel.kt`: state (city, weather, loading, error, stale), `searchCity(city)`, `refresh()`, uses `WeatherRepository`
- [ ] T049 [US3] Create `MainAppScreen` in `android/app/src/main/java/com/weatherender/widget/ui/app/MainAppScreen.kt` (Compose, Material 3): `Scaffold` with top bar (city input + search button), body with condition gradient background (`Box` + `Modifier.background`), main card (hero temp, city, condition pill), stat pills row (UV, Humidity, Pressure), snow status, 3 day cards horizontal scroll (date, avg temp, precip, precip chance, UV, wind, gusts, snow), stale indicator + manual refresh button
- [ ] T050 [US3] Register `MainActivity` in `AndroidManifest.xml` as launcher activity (`android:exported="true"`, intent-filter MAIN/LAUNCHER), theme `Theme.Material3.DayNight.NoActionBar`
- [ ] T051 [US3] Implement city search: text field + search button → `MainViewModel.searchCity()` → show loading → on success update UI → on error show snackbar/error message with retry
- [ ] T052 [US3] Implement pull-to-refresh / manual refresh button → `WidgetRefreshScheduler.scheduleImmediateRefresh(city)` → `MainViewModel.refresh()`
- [ ] T053 [US3] Implement offline/cached state: show stale indicator with timestamp, disable search until network returns (or allow search with network check)

---

## Phase 6: User Story 4 - Visual Style Parity (Priority: P2)

**Goal**: Widgets and app screen match web UI visual design (gradients, card styling, typography, semi-transparent cards with white borders)

**Independent Test**: Side-by-side screenshot comparison of widget vs web UI and app screen vs web UI for all 4 conditions (sunny, rainy, cloudy, thunder) shows matching colors, spacing, card opacity, border, radius

### Implementation for User Story 4

- [ ] T054 [US4] Apply `ConditionTheme` gradients as background in both `CurrentWeatherWidget`, `ForecastWidget`, and `MainAppScreen` (full background = condition gradient)
- [ ] T055 [US4] Apply `WidgetCardStyle` constants to all cards in both widgets and app screen (main card, inner blocks, stat pills, day cards)
- [ ] T056 [US4] Apply `WidgetTypography` scale to all text elements in both widgets and app screen
- [ ] T057 [P] [US4] Create screenshot comparison test in `android/app/src/androidTest/.../ui/VisualParityTest.kt` using `createComposeRule` + Glance testing to render widgets and app screen with mocked data for each condition and verify color values

---

## Phase 7: User Story 5 - Widget Configuration (Priority: P2)

**Goal**: Configuration activity for city input with validation

**Independent Test**: Long-press widget → configure → enter city → save → widget updates; invalid city shows error

### Implementation for User Story 5

- [ ] T058 [US5] Complete `WidgetConfigActivity` (T028) with full validation: non-blank, 1-100 chars, URL-encode city name for API
- [ ] T059 [US5] Store widget ID ↔ city mapping in DataStore (per widget instance)
- [ ] T060 [US5] On config save: trigger immediate `WeatherRefreshWorker` for that city → update widget
- [ ] T061 [P] [US5] Unit test `WidgetConfigActivity` validation logic in `android/app/src/test/.../config/WidgetConfigActivityTest.kt`

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements affecting multiple user stories

- [ ] T062 [P] Add unit tests for `WidgetRefreshScheduler` periodic/immediate scheduling
- [ ] T063 [P] Add integration test for full flow: config → API → cache → widget/app render (Robolectric)
- [ ] T064 [P] Document build steps in `android/README.md`: copy `local.properties.template`, set API URL, `./gradlew :app:assembleDebug`
- [ ] T065 Update root `CHANGELOG.md` with Android app + widgets feature entry
- [ ] T066 [P] Verify ProGuard/R8 rules for Retrofit, Room, Glance, Compose, Serialization
- [ ] T067 Run `./gradlew :app:lint` and fix warnings
- [ ] T068 Run full test suite: `./gradlew :app:test :app:connectedAndroidTest`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - **BLOCKS all user stories**
- **User Stories (Phase 3-7)**: All depend on Foundational phase completion
  - US1 (Current Widget) and US2 (Forecast Widget) can proceed in parallel after Foundational
  - US3 (Main App Screen) can proceed in parallel after Foundational (shares data layer)
  - US4 (Visual Parity) applies to both widgets and app - can start after US1/US2/US3 structures exist
  - US5 (Configuration) needed by both widgets - can start after Foundational
- **Polish (Phase 8)**: Depends on all desired user stories being complete

### User Story Dependencies

- **US1 (P1)**: Can start after Foundational - No dependencies on other stories
- **US2 (P1)**: Can start after Foundational - Independent widget, shares data layer
- **US3 (P1)**: Can start after Foundational - Independent app screen, shares data layer
- **US4 (P2)**: Requires US1, US2, and US3 widget/app structures to apply theming
- **US5 (P2)**: Required by US1 and US2 for configuration - should complete early

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Models before services/widgets/app
- Widget/app implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, US1, US2, and US3 can start in parallel
- All test tasks marked [P] within a story can run in parallel
- US4 theming tasks can parallelize across both widget files and app screen

---

## Parallel Example: Foundational Phase

```bash
# Launch all [P] tasks in Phase 2 together:
Task: T009 Create typed API DTOs
Task: T010 Create domain models
Task: T013 Create Room entity
Task: T014 Create DAO
Task: T017 Create WidgetPreferences DataStore
Task: T018 Create AppPreferences DataStore
Task: T019 Create ConditionTheme
Task: T020 Create WidgetCardStyle
Task: T021 Create WidgetTypography
Task: T023 Create ConditionThemeMapper
Task: T024 Create DateFormatter
Task: T026 Create WidgetRefreshScheduler
Task: T038 Unit test forecast mapping
Task: T039 Unit test date formatting
Task: T061 Unit test config validation
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (Current Weather Widget)
4. **STOP and VALIDATE**: Test US1 independently on device/emulator
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add US1 (Current Widget) → Test independently → Deploy/Demo (MVP!)
3. Add US2 (Forecast Widget) → Test independently → Deploy/Demo
4. Add US3 (Main App Screen) → Test independently → Deploy/Demo
5. Add US4 (Visual Parity) → Polish all three → Deploy/Demo
6. Add US5 (Configuration) → Already needed by US1/US2, do early
7. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: US1 (Current Widget)
   - Developer B: US2 (Forecast Widget)
   - Developer C: US3 (Main App Screen)
   - Developer D: US4 (Theming) + US5 (Configuration)
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
