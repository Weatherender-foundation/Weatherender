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

- [ ] T001 Create `android/` module directory structure: `app/src/main/java/com/weatherender/widget/{ui,data}` + subpackages
- [ ] T002 Create `android/build.gradle.kts` with Compose BOM, plugins, dependency versions
- [ ] T003 Create `android/app/build.gradle.kts` with approved dependencies only (NO Retrofit, NO Room, NO Hilt, NO Navigation)
- [ ] T004 Create `android/app/src/main/AndroidManifest.xml` with INTERNET permission, two widget providers, main activity, config activity
- [ ] T005 Create `android/app/src/main/res/xml/current_widget_info.xml` (Glance metadata, previewImage, minResize 2x2)
- [ ] T006 Create `android/app/src/main/res/xml/forecast_widget_info.xml` (Glance metadata, previewImage, minResize 4x2)
- [ ] T007 Create `android/app/src/main/res/values/strings.xml` (app name, widget labels, errors)
- [ ] T008 Create `android/app/src/main/res/values/colors.xml` (gradient colors from web CSS)
- [ ] T009 Create `android/app/src/main/res/values/themes.xml` (Material 3 theme)
- [ ] T010 Create `android/local.properties.template` (document `API_BASE_URL` for `buildConfigField`)

---

## Phase 2: Foundational Data Layer

- [ ] T011 [P] Create `data/api/WeatherApiDto.kt` — all `@Serializable` DTOs matching `WeatherResponseV2` (LocationDto, ConditionDto, CurrentDto, DayDto, HourDto, ForecastDayDto, ForecastDto, SnowStateDto, SnowForecastDto, AttributionDto, WeatherResponseDto)
- [ ] T012 [P] Create `data/api/ApiClient.kt` — OkHttpClient singleton, `fetchWeather(city: String): Result<WeatherResponseDto>`, base URL from `BuildConfig.API_BASE_URL`, kotlinx-serialization parsing
- [ ] T013 [P] Create `data/config/Preferences.kt` — DataStore Preferences: `WidgetPreferences` (city_<appWidgetId>), `AppPreferences` (app_city, app_unit_metric, timestamps)
- [ ] T014 [P] Create `data/cache/JsonFileCache.kt` — `saveRawResponse(city, json)`, `loadRawResponse(city)`, `getLastModified(city)` using `context.cacheDir`
- [ ] T015 Create `data/repository/WeatherRepository.kt` — `getWeather(city, forceRefresh)`: cache check → API call → cache write → parse; returns `Result<WeatherResponseDto>`
- [ ] T016 Create `data/worker/WeatherRefreshWorker.kt` — CoroutineWorker, input (city, appWidgetId?, isManual), calls repository, updates Glance widgets on success
- [ ] T017 [P] Create `data/worker/WorkScheduler.kt` — `schedulePeriodicRefresh(city)`, `enqueueImmediateRefresh(city, appWidgetId?)`, 30-min PeriodicWorkRequest

---

## Phase 3: US1 — Current Weather Widget (Glance)

- [ ] T018 [P] Create `ui/theme/ConditionTheme.kt` — `ConditionTheme` data class, `ConditionThemeMapper.map(conditionText)` with FR-006 precedence
- [ ] T019 [P] Create `ui/theme/Gradients.kt` — 4 gradient definitions matching `style.css` exactly
- [ ] T020 [P] Create `ui/theme/CardStyles.kt` — `weatherCardModifier`, `blockModifier`, `dayCardModifier`
- [ ] T021 Create `ui/current/CurrentWidget.kt` — `CurrentWidgetReceiver : GlanceAppWidget()`, `provideGlance()` → `CurrentWidgetContent`
- [ ] T022 Create `ui/current/CurrentWidgetContent.kt` — Glance composable: gradient bg, hero temp (°C), condition, UV, humidity, pressure mmHg, snow status, loading/error/stale states
- [ ] T023 [P] Create `ui/current/CurrentWidgetState.kt` — data class for widget state (data, error, lastUpdated)
- [ ] T024 [P] Write unit tests: `ConditionThemeMapperTest` (thunder→cloudy→rainy→sunny precedence), `CurrentWidgetContentTest` (Glance rendering)

---

## Phase 4: US2 — 3-Day Forecast Widget (Glance)

- [ ] T025 Create `ui/forecast/ForecastWidget.kt` — `ForecastWidgetReceiver : GlanceAppWidget()`, `provideGlance()` → `ForecastWidgetContent`
- [ ] T026 Create `ui/forecast/ForecastWidgetContent.kt` — 3 day cards: date DD.MM.YYYY, avg temp °C, precip mm, precip chance %, UV, wind km/h, gusts km/h (fallback maxwind*1.2), snow state
- [ ] T027 [P] Create `ui/forecast/ForecastWidgetState.kt` — data class for widget state
- [ ] T028 [P] Write unit tests: `ForecastWidgetContentTest`, gusts fallback logic test

---

## Phase 5: US5 — Widget Configuration (Compose Activity)

- [ ] T029 Create `ui/config/WidgetConfigActivity.kt` — `ComponentActivity`, `setContent { WidgetConfigScreen(...) }`, reads `appWidgetId` from intent
- [ ] T030 Create `ui/config/WidgetConfigScreen.kt` — Compose screen: TextField (1-100 chars, non-blank), Save button, error display, onSave → DataStore `city_<appWidgetId>` → enqueue `WeatherRefreshWorker` → `setResult(RESULT_OK)` → `finish()`
- [ ] T031 [P] Write unit test: `WidgetConfigScreenTest` (validation, save flow)

---

## Phase 6: US3 — Main App Screen (Compose)

- [ ] T032 [P] Create `ui/app/MainScreenViewModel.kt` — `ViewModel`, `UiState` (data, hourly, daily, loading, error, unitMetric, city), `searchCity()`, `toggleUnits()`, `refresh()`, DataStore persistence
- [ ] T033 Create `ui/app/MainActivity.kt` — `ComponentActivity`, `MainScreen(viewModel)`, handles search intent
- [ ] T034 [P] Create `ui/app/components/WeatherCard.kt` — semi-transparent card with white border, rounded corners, theme-aware
- [ ] T035 [P] Create `ui/app/components/StatPill.kt` — label + value pill
- [ ] T036 [P] Create `ui/app/components/HeroTemp.kt` — large temperature display
- [ ] T037 [P] Create `ui/app/components/DayCard.kt` — daily forecast card
- [ ] T038 Create `ui/app/components/UnitToggleButton.kt` — button with exact labels: metric=`°C / °F | km/h / mph`, imperial=`°F / °C | mph / km/h`
- [ ] T039 Create `ui/app/MainScreen.kt` — Scaffold: gradient bg, top bar (city input + unit toggle), content (current hero, hourly placeholder, daily cards), loading/error/empty states
- [ ] T040 [P] Write unit tests: `MainScreenViewModelTest` (search, toggle, refresh, persistence)

---

## Phase 7: US4 — Visual Style Parity (Static)

- [ ] T041 [P] Create `ui/theme/Typography.kt` — font sizes, weights matching web UI (clamp, letter-spacing)
- [ ] T042 [P] Verify all 4 condition gradients render correctly in widgets and app (manual checklist)
- [ ] T043 [P] Verify card styling parity: semi-transparent dark bg, white borders, rounded corners, typography scale
- [ ] T044 [P] Write Compose UI tests: `MainScreenTest` (theme switching, card rendering)

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

## Phase 9: US7 — 24-Hour Forecast Table (P3, Skippable)

- [ ] T057 [P] Create `ui/app/components/HourlyForecastTable.kt` — LazyColumn with sticky header, 24 rows: Time, Temp (unit-aware), Precip %, UV, Pressure mmHg
- [ ] T058 Update `ui/app/MainScreenViewModel.kt` — add `hourlyForecast` computation: parse `location.localtime` → current hour → flatten forecast hours → filter ≥ current → take 24 → map to `HourlyRow`
- [ ] T059 [P] Update `ui/app/MainScreen.kt` — insert `HourlyForecastTable` between current hero and daily cards
- [ ] T060 [P] Write unit test: `HourlyForecastTest` — 24h window selection, pressure conversion, unit toggle affects temp only

---

## Phase 10: US8 — Unit Toggle with DataStore Persistence (P3, Skippable)

- [ ] T061 [P] Update `data/config/Preferences.kt` — `APP_UNIT_METRIC_KEY`, `isMetric` getter/setter
- [ ] T062 Update `ui/app/MainScreenViewModel.kt` — load `unitMetric` from DataStore on init, `toggleUnits()` saves to DataStore
- [ ] T063 [P] Update `ui/app/components/UnitToggleButton.kt` — ensure exact label strings match web `units.js`
- [ ] T064 [P] Update all display components (`HeroTemp`, `StatPill`, `DayCard`, `HourlyForecastTable`) to branch on `unitMetric`:
  - Temp: `metric ? temp_c : temp_f`
  - Wind/Gusts: `metric ? wind_kph : wind_mph`
  - Pressure: always mmHg
  - Precipitation: always mm
- [ ] T065 [P] Write unit test: `UnitToggleTest` — toggle persists, widgets unaffected (always metric), labels match exactly

---

## Cross-Cutting & Verification

- [ ] T066 [P] Create `quickstart.md` — build setup, `local.properties` (API_BASE_URL), manual visual parity checklist (replaces screenshot tests)
- [ ] T067 Run `./gradlew :android:app:testDebugUnitTest` — all unit tests pass
- [ ] T068 Run `./gradlew :android:app:assembleDebug` — build succeeds, no forbidden dependencies
- [ ] T069 Manual verification: add both widgets, configure cities, verify current/forecast data, tap refresh
- [ ] T070 Manual verification: launch app, search city, verify current + daily + (if P3 done) hourly + unit toggle
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
T021-T024 (Current Widget)     T025-T028 (Forecast Widget) ← parallel phases
    ↓
T029-T031 (Widget Config)
    ↓
T032-T040 (Main App Screen)
    ↓
T041-T044 (Visual Parity)
    ↓
T045-T056 (Ambient Effects - P3)  ← INDEPENDENT, SKIPPABLE
    ↓
T057-T060 (Hourly Table - P3)     ← INDEPENDENT, SKIPPABLE
    ↓
T061-T065 (Unit Toggle - P3)      ← INDEPENDENT, SKIPPABLE
    ↓
T066-T074 (Verification)
```

---

## Notes

- **Phases 8-10 (T045-T065) are P3 and independently skippable** — they can be dropped without blocking T066-T074
- **No Retrofit tasks** — T012 uses pure OkHttp
- **No Room tasks** — T013-T014 use DataStore + JsonFileCache
- **JUnit 4 only** — all test tasks specify JUnit 4 + Robolectric
- **No XML layout for config** — T029-T030 are 100% Compose
- **Widget preview images** — T005-T006 use `previewImage` in widget XML (no layout preview)
