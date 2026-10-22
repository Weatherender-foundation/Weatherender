# Implementation Tasks: Android Weather Widgets & App

**Branch**: `feature/android`
**Spec**: `specs/001-android-weather-widgets/spec.md`
**Plan**: `specs/001-android-weather-widgets/plan.md`

---

## Task Format
- `[ ] T### [P] Description` — `P` = parallelizable (no dependency on other tasks in same phase)
- Tasks ordered by phase; dependencies flow top-to-bottom
- Phase headers mark logical groupings

---

## Phase 1: Setup

- [x] T001 Create `android/` module directory structure: `app/src/main/java/com/weatherender/widget/{ui,data}` + subpackages
- [x] T002 Create `android/build.gradle.kts` with Compose BOM, plugins, dependency versions
- [x] T003 Create `android/app/build.gradle.kts` with approved dependencies only (NO Retrofit, NO Room, NO Hilt, NO Navigation)
- [x] T004 Create `android/app/src/main/AndroidManifest.xml` with INTERNET permission, two widget providers, main activity, config activity
- [x] T005 Create `android/app/src/main/res/xml/current_widget_info.xml` (Glance metadata, previewImage, minResize 2x2)
- [x] T006 Create `android/app/src/main/res/xml/forecast_widget_info.xml` (Glance metadata, previewImage, minResize 4x2)
- [x] T007 Create `android/app/src/main/res/values/strings.xml` (app name, widget labels, errors)
- [x] T008 Create `android/app/src/main/res/values/colors.xml` (gradient colors from web CSS)
- [x] T009 Create `android/app/src/main/res/values/themes.xml` (Material 3 theme)
- [x] T010 Create `android/local.properties.template` (document `API_BASE_URL` for `buildConfigField`)

---

## Phase 2: Foundational Data Layer

- [x] T011 [P] Create `data/api/WeatherApiDto.kt` — all `@Serializable` DTOs matching `WeatherResponseV2` (LocationDto, ConditionDto, CurrentDto, DayDto, HourDto, ForecastDayDto, ForecastDto, SnowStateDto, SnowForecastDto, AttributionDto, WeatherResponseDto)
- [x] T012 [P] Create `data/api/ApiClient.kt` — OkHttpClient singleton, `fetchWeather(city: String): Result<WeatherResponseDto>`, base URL from `BuildConfig.API_BASE_URL`, kotlinx-serialization parsing
- [x] T013 [P] Create `data/config/Preferences.kt` — DataStore Preferences: `WidgetPreferences` (city_<appWidgetId>), `AppPreferences` (app_city, app_unit_metric, timestamps)
- [x] T014 [P] Create `data/cache/JsonFileCache.kt` — `saveRawResponse(city, json)`, `loadRawResponse(city)`, `getLastModified(city)` using `context.cacheDir`
- [x] T015 Create `data/repository/WeatherRepository.kt` — `getWeather(city, forceRefresh)`: cache check → API call → cache write → parse; returns `Result<WeatherResponseDto>`
- [x] T016 Create `data/worker/WeatherRefreshWorker.kt` — CoroutineWorker, input (city, appWidgetId?, isManual), calls repository, updates Glance widgets on success
- [x] T017 [P] Create `data/worker/WorkScheduler.kt` — `schedulePeriodicRefresh(city)`, `enqueueImmediateRefresh(city, appWidgetId?)`, 30-min PeriodicWorkRequest

---

## Phase 3: US1 — Current Weather Widget (Glance)

- [x] T018 [P] Create theme infrastructure: `WeatherTheme.kt` with condition mapping (thunder→cloudy→rainy→sunny precedence), 4 gradients matching CSS
- [x] T019 [P] Create card styles: `WeatherCard`, `BlockCard`, `HeroCard`, `StatPillCard`, `DayCard` in `WeatherTheme.kt`
- [x] T020 [P] Create UI components: `HeroTemp`, `StatPill`, `ConditionPill`, `Eyebrow`, `Title`, etc. in `CommonComponents.kt`
- [x] T021 **Implement `CurrentWeatherWidget` Glance widget** — full UI: gradient bg, hero temp (°C), condition, UV, humidity, pressure mmHg, snow status, loading/error/stale states (currently minimal)
- [x] T022 [P] Write unit tests: `CurrentWeatherWidgetTest` (Glance rendering, theme mapping)

---

## Phase 4: US2 — 3-Day Forecast Widget (Glance)

- [x] T023 **Implement `ForecastWeatherWidget` Glance widget** — 3 day cards: date DD.MM.YYYY, avg temp °C, precip mm, precip chance %, UV, wind km/h, gusts km/h (fallback maxwind*1.2), snow state (currently minimal)
- [x] T024 [P] Write unit tests: `ForecastWeatherWidgetTest`, gusts fallback logic test

---

## Phase 5: US5 — Widget Configuration (Compose Activity)

- [x] T025 **Implement `WidgetConfigActivity` with full Compose UI** — TextField (1-100 chars, non-blank), Save button, error display, onSave → DataStore `city_<appWidgetId>` → enqueue `WeatherRefreshWorker` → `setResult(RESULT_OK)` → `finish()` (currently just shows "Configure Weather Widget")
- [x] T026 [P] Write unit test: `WidgetConfigScreenTest` (validation, save flow)

---

## Phase 6: US3 — Main App Screen (Compose) — COMPLETED

- [x] T032 MainViewModel with UiState, searchCity(), toggleUnits(), refresh(), DataStore persistence
- [x] T033 MainActivity with MainScreen, handles search intent
- [x] T034 WeatherCard, BlockCard, HeroCard, StatPillCard, DayCard in WeatherTheme.kt
- [x] T035 StatPill in CommonComponents.kt
- [x] T036 HeroTemp in CommonComponents.kt
- [x] T037 DayCard in CommonComponents.kt
- [x] T038 UnitToggle in CommonComponents.kt with exact labels
- [x] T039 MainScreen with gradient bg, top bar (city input + unit toggle), current hero, hourly table, daily cards, loading/error/empty states
- [x] T040 [P] Unit tests for MainViewModel

---

## Phase 7: US4 — Visual Style Parity (Static) — COMPLETED

- [x] T041 Typography in WeatherTheme.kt (font sizes, weights matching web UI)
- [x] T042 4 condition gradients matching web CSS
- [x] T043 Card styling parity: semi-transparent dark bg, white borders, rounded corners
- [x] T044 Compose UI components match web UI visual style

---

## Phase 8: US6 — Ambient Background Orbs & Canvas Atmospheric Effects (P3, Skippable)

- [ ] T045 [P] Create `ui/effects/AmbientOrbs.kt` — two Canvas radial-gradient orbs, 18s drift alternate, theme-specific colors from CSS
- [ ] T046 [P] Create `ui/effects/HeroOrb.kt` — Canvas radial-gradient orb in hero card, 9s orbFloat alternate
- [ ] T047 Create `ui/effects/AtmosphericEffects.kt` — sealed interface `AtmosphericEffect`, implementations: `SunnyEffects` (SunGlow + Particles), `RainyEffects` (Clouds + RainDrops), `CloudyEffects` (Clouds), `ThunderEffects` (Clouds + Lightning + Stars)
- [ ] T048 [P] Create `ui/effects/SunGlow.kt` — radial gradient, 0.4s opacity fade-in
- [ ] T049 [P] Create `ui/effects/Particles.kt` — bounded particle count (max 50), 12s linear drift, `rememberInfiniteTransition`
- [ ] T050 [P] Create `ui/effects/Clouds.kt` — 2 clouds, 18s floatCloud alternate, theme opacity
- [ ] T051 [P] Create `ui/effects/RainDrops.kt` — 3 streaks, 1.2s rainFall linear infinite
- [ ] T052 [P] Create `ui/effects/Lightning.kt` — triangle shape, 6s lightningFlash (0→1→0.2→1→0)
- [ ] T053 [P] Create `ui/effects/Stars.kt` — radial gradients, 4s twinkle alternate
- [ ] T054 Create `ui/effects/AnimationController.kt` — `LifecycleObserver` pausing all `InfiniteTransition` on ON_PAUSE, resuming on ON_RESUME; checks `Settings.Global.ANIMATOR_DURATION_SCALE == 0f` to disable
- [ ] T055 Create `ui/effects/FloatInAnimation.kt` — `AnimatedVisibility` with fadeIn+slideIn (0.55s ease), staggered delays (0s, 0.08s, 0.12s)
- [ ] T056 [P] Write unit tests: `AtmosphericEffectsTest` — particle cap enforcement, animator scale=0 disables all, pause/resume lifecycle

---

## Phase 9: US7 — 24-Hour Forecast Table (P3, Skippable) — COMPLETED

- [x] T057 HourlyForecastTable with LazyColumn, sticky header, 24 rows: Time, Temp (unit-aware), Precip %, UV, Pressure mmHg
- [x] T058 MainViewModel includes hourly forecast computation
- [x] T059 MainScreen includes HourlyForecastTable
- [x] T060 [P] Unit test for hourly forecast

---

## Phase 10: US8 — Unit Toggle with DataStore Persistence (P3, Skippable) — COMPLETED

- [x] T061 Preferences.kt has APP_UNIT_METRIC_KEY, isMetric getter/setter
- [x] T062 MainViewModel loads unitMetric from DataStore on init, toggleUnits() saves to DataStore
- [x] T063 UnitToggle button with exact label strings from web units.js
- [x] T064 All display components branch on unitMetric (temp, wind/gusts, pressure mmHg, precip mm)
- [x] T065 [P] Unit test for unit toggle

---

## Cross-Cutting & Verification

- [ ] T066 [P] Create `quickstart.md` — build setup, `local.properties` (API_BASE_URL), manual visual parity checklist
- [ ] T067 Run `./gradlew :android:app:testDebugUnitTest` — all unit tests pass
- [ ] T068 Run `./gradlew :android:app:assembleDebug` — build succeeds, no forbidden dependencies
- [ ] T069 Manual verification: add both widgets, configure cities, verify current/forecast data, tap refresh
- [ ] T070 Manual verification: launch app, search city, verify current + daily + hourly + unit toggle
- [ ] T071 Manual verification: visual parity — compare widget/app gradients, cards, typography to web UI for all 4 conditions
- [ ] T072 Manual verification: offline mode — disable network, verify cached data + stale indicator
- [ ] T073 Manual verification: system animations off — verify atmospheric effects disabled
- [ ] T074 Update `CHANGELOG.md` with dated entry for this feature

---

## Task Dependency Graph (Critical Path)

```
T001-T010 (Setup)
    ↓
T011-T017 (Data Layer) ← parallel within phase
    ↓
T018-T020 (Theme Foundation) ← parallel
    ↓
T021-T022 (Current Widget)     T023-T024 (Forecast Widget) ← parallel phases
    ↓
T025-T026 (Widget Config)
    ↓
T032-T040 (Main App Screen) ← COMPLETED
    ↓
T041-T044 (Visual Parity) ← COMPLETED
    ↓
T045-T056 (Ambient Effects - P3)  ← INDEPENDENT, SKIPPABLE
    ↓
T057-T060 (Hourly Table - P3)     ← COMPLETED
    ↓
T061-T065 (Unit Toggle - P3)      ← COMPLETED
    ↓
T066-T074 (Verification)
```

---

## Notes

- **Phases 8 (T045-T056) are P3 and independently skippable** — they can be dropped without blocking T066-T074
- **No Retrofit tasks** — T012 uses pure OkHttp
- **No Room tasks** — T013-T014 use DataStore + JsonFileCache
- **JUnit 4 only** — all test tasks specify JUnit 4 + Robolectric
- **No XML layout for config** — T025 is 100% Compose
- **Widget preview images** — T005-T006 use `previewImage` in widget XML (no layout preview)
- **Package structure** is flat: `com.weatherender.widget.ui` and `com.weatherender.widget.data` (no subpackages)