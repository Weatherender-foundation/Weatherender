# Feature Specification: Android App with Main Screen and Widgets

**Feature Branch**: `feature/android`

**Created**: 2026-10-18

**Status**: Draft/Pending Approval

**Input**: User description: "Android app in ./android (Kotlin only) with a full app screen in the web UI visual style AND static widgets showing current weather and forecast from the existing Flask/FastAPI endpoints. No changes to the Python code."

## User Scenarios & Testing

### User Story 1 - View Current Weather Widget (Priority: P1)

As an Android user, I want to add a home screen widget that displays current weather conditions for a configured city so that I can see temperature, condition, and key metrics at a glance without opening an app.

**Why this priority**: Core widget functionality delivering immediate value; widgets are the primary feature request.

**Independent Test**: Can be fully tested by adding the widget to home screen, configuring a city, and verifying current weather data displays correctly from the API endpoint.

**Acceptance Scenarios**:

1. **Given** the widget is added to home screen and a valid city is configured, **When** the widget refreshes, **Then** it displays current temperature, weather condition text, UV index, humidity, pressure, and snow condition from the `/api/v2/weather` endpoint.
2. **Given** the widget is configured with a city, **When** network is available, **Then** data loads within 5 seconds and shows a loading state until complete.
3. **Given** the API returns an error (city not found, network failure), **When** the widget attempts to refresh, **Then** it displays a user-friendly error message and retains the last successful data.

---

### User Story 2 - View 3-Day Forecast Widget (Priority: P1)

As an Android user, I want a separate home screen widget showing a 3-day forecast with daily average temperature, precipitation chance, UV index, wind, and snow conditions so that I can plan ahead.

**Why this priority**: Forecast is equally core to weather utility; users expect multi-day outlook.

**Independent Test**: Can be fully tested by adding the forecast widget, configuring a city, and verifying 3 days of forecast data displays correctly.

**Acceptance Scenarios**:

1. **Given** the forecast widget is added and configured, **When** it refreshes, **Then** it displays 3 days of forecast data (date, average temperature, precipitation mm, precipitation chance %, UV index, wind speed, gusts, snow state) from the `/api/v2/weather` endpoint.
2. **Given** forecast data is available, **When** displayed, **Then** each day shows the correct date format (DD.MM.YYYY) and units match the web UI (Celsius, km/h, mm).

---

### User Story 3 - Main App Screen (Priority: P1)

As an Android user, I want a full-screen app in the web UI visual style with city input/search, current weather, and 3-day forecast so that I can view detailed weather information when I open the app.

**Why this priority**: The app screen provides the full weather experience with city search, matching the web UI's visual design language (gradients, cards, typography, atmospheric animated effects). It reuses the same data layer as widgets.

**Independent Test**: Can be fully tested by launching the app, entering a city name, and verifying current weather and 3-day forecast display correctly with web UI visual parity including animated effects.

**Acceptance Scenarios**:

1. **Given** the app is launched, **When** the user enters a valid city name and searches, **Then** the app displays current weather (temperature, condition, UV, humidity, pressure, snow) and 3-day forecast (date, avg temp, precip mm, precip chance %, UV, wind, gusts, snow) with condition-based gradient background.
2. **Given** the user enters an invalid city name, **When** they search, **Then** the app displays a user-friendly error message (city not found) and allows retry.
3. **Given** the device is offline, **When** the app launches with a previously configured city, **Then** it displays the last cached weather data with a clear "offline/stale data" indicator showing the last update timestamp.
4. **Given** any condition (sunny, rainy, cloudy, thunder), **When** the app renders, **Then** it uses the correct gradient background and the same semi-transparent card styling with white borders and rounded corners as the web UI.
5. **Given** the app screen is displayed, **When** viewed, **Then** it does NOT include cursor/touch-following tilt, parallax, or 3D rotation of the window or cards.
6. **Given** a "sunny" condition, **When** the app renders, **Then** it reproduces the web UI's sunny atmospheric effects: static sun-glow radial glow + drifting particles (based on `.sun-glow` and `.particles` CSS).
7. **Given** a "rainy" condition, **When** the app renders, **Then** it reproduces the web UI's rainy atmospheric effects: 2 drifting clouds + 3 falling rain streaks (based on `.cloud` and `.rain` CSS).
8. **Given** a "cloudy" condition, **When** the app renders, **Then** it reproduces the web UI's cloudy atmospheric effects: 2 drifting clouds (based on `.cloud` CSS).
9. **Given** a "thunder" condition, **When** the app renders, **Then** it reproduces the web UI's thunder atmospheric effects: 2 drifting clouds + lightning flash every ~6s + twinkling stars (based on `.cloud`, `.lightning`, and `.stars` CSS).
10. **Given** the app is moved to background, **When** the app pauses, **Then** all atmospheric animations pause to conserve battery.
11. **Given** the system "remove animations" setting is enabled, **When** the app renders, **Then** all atmospheric animations are disabled and only static gradients/cards are shown.
12. **Given** a low-end device, **When** the app runs atmospheric animations, **Then** the particle count is bounded to maintain smooth performance (no visible stutter).

---

### User Story 4 - Visual Style Parity with Web UI (Priority: P2)

As an Android user, I want the widgets and app screen to match the web UI's visual design language so that the experience feels cohesive across platforms.

**Why this priority**: Brand consistency and user trust; the web UI has a distinctive atmospheric design (gradients, cards, typography, animated effects) that should translate to both widgets (static) and app screen (animated).

**Independent Test**: Can be verified by side-by-side visual comparison of widget screenshots (static), app screen screenshots (animated), and web UI screenshots for the same weather conditions.

**Acceptance Scenarios**:

1. **Given** a "sunny" condition, **When** the widget or app renders, **Then** it uses the sunny gradient background (#4facfe to #00f2fe).
2. **Given** a "rainy" condition, **When** the widget or app renders, **Then** it uses the rainy gradient (#0f2027 to #2c5364).
3. **Given** a "cloudy" condition, **When** the widget or app renders, **Then** it uses the cloudy gradient (#4b5d7d to #edf2ff).
4. **Given** a "thunder" condition, **When** the widget or app renders, **Then** it uses the thunder gradient (#0a1220 to #2c3e50).
5. **Given** any condition, **When** the widget or app renders, **Then** it uses the same card styling: semi-transparent dark cards with white borders, rounded corners, and a typography scale matching the web UI.
6. **Given** the app screen with any condition, **When** rendered, **Then** the atmospheric animated effects (sun glow/particles, rain/clouds, lightning/stars) match the web UI's CSS/JS implementations in behavior and visual appearance.

---

### User Story 5 - Widget Configuration (Priority: P2)

As an Android user, I want to configure the widget's target city through a configuration activity so that I can see weather for my location of choice.

**Why this priority**: Widgets must be configurable per Android platform requirements; users need to set their city.

**Independent Test**: Can be tested by long-pressing widget → configure → entering city name → saving → widget updates to show new city.

**Acceptance Scenarios**:

1. **Given** the user adds the widget, **When** they tap the configuration area, **Then** a configuration activity opens allowing city name input.
2. **Given** a valid city is entered and saved, **When** the widget refreshes, **Then** it fetches and displays weather for that city.
3. **Given** an invalid city is entered, **When** saved, **Then** the configuration shows an error and does not save.

---

### User Story 6 - Ambient Animated Background (Priority: P3, Skippable)

As an Android user, I want the app screen to have an ambient animated background matching the web UI so that the atmospheric feel is preserved.

**Why this priority**: The web UI's drifting colored orbs and hero-card light orb are signature visual elements that define the brand's atmospheric aesthetic. This story is independent and can be skipped without blocking other features.

**Independent Test**: Can be verified by observing the app screen background for 20+ seconds: two soft-edged (radial-gradient) colored orbs drifting on an 18s alternate cycle, hero-card light orb on a 9s cycle, and entrance float-in of content blocks.

**Acceptance Scenarios**:

1. **Given** the app screen is visible, **When** it renders, **Then** two soft-edged (radial-gradient) drifting colored orbs (body::before/::after) animate on an 18s ease-in-out infinite alternate cycle with theme-specific colors: sunny (rgba(255,212,87,0.38) / rgba(142,224,255,0.44)), rainy (rgba(95,176,255,0.35) / rgba(169,223,255,0.3)), cloudy (rgba(255,255,255,0.45) / rgba(164,181,217,0.38)), thunder (rgba(117,130,255,0.28) / rgba(140,168,255,0.2)).
2. **Given** the app screen is visible, **When** the hero card (weather-hero) renders, **Then** its inner light orb (weather-hero::before) animates on a 9s ease-in-out infinite alternate cycle (orbFloat keyframes).
3. **Given** the app screen loads, **When** content appears, **Then** the current/hourly/daily blocks animate with floatIn (0.55s ease) with staggered delays (0s, 0.08s, 0.12s).
4. **Given** the app is backgrounded, **When** paused, **Then** all ambient animations pause.
5. **Given** the system "remove animations" setting is enabled, **When** the app renders, **Then** all ambient animations are disabled (instant appearance, no drift).

---

### User Story 7 - Hourly Forecast (Priority: P3, Skippable)

As an Android user, I want to see a 24-hour forecast table in the app screen so that I can plan my day hour by hour.

**Why this priority**: The web UI provides a detailed hourly table; the app screen should match this capability. This story is independent and can be skipped without blocking other features.

**Independent Test**: Can be tested by launching the app, searching a city, and verifying the hourly table displays the next 24 hours from the city's local time with Time, Temperature, Precipitation %, UV Index, Pressure columns; unit toggle affects temperature only (not pressure).

**Acceptance Scenarios**:

1. **Given** the app displays weather for a city, **When** the hourly section is visible, **Then** it shows a table with columns: Time, Temperature, Precipitation %, UV Index, Pressure (pressure in mmHg, computed from pressure_mb × 0.750062).
2. **Given** the unit toggle is in metric mode, **When** the hourly table renders, **Then** Temperature shows °C and Pressure shows mmHg.
3. **Given** the unit toggle is in imperial mode, **When** the hourly table renders, **Then** Temperature shows °F and Pressure shows mmHg (unchanged).
4. **Given** the app is offline with cached data, **When** the hourly table renders, **Then** it shows cached hourly data with stale indicator.
5. **Given** the city's local time is near midnight, **When** the hourly table renders, **Then** it shows the next 24 hours spanning today and tomorrow (using location.localtime / tz_id from the API). If fewer than 24 hourly entries remain in the response, it shows the remaining hours.

---

### User Story 8 - Unit Toggle (Priority: P3, Skippable)

As an Android user, I want to toggle between metric and imperial units for temperature and wind speed in the app screen so that I can view them in my preferred system.

**Why this priority**: The web UI has a unit toggle; the app screen must match this functionality for parity. This story is independent and can be skipped without blocking other features.

**Independent Test**: Can be tested by launching the app, searching a city, tapping the unit toggle, and verifying temperatures (°C↔°F) and wind speeds including gusts (km/h↔mph) update instantly across current weather, hourly table, and daily forecast cards; pressure and precipitation remain unchanged.

**Acceptance Scenarios**:

1. **Given** the app displays weather, **When** the unit toggle is tapped, **Then** all temperature values switch between the API-provided °C and °F values (temp_c/temp_f, avgtemp_c/avgtemp_f, feelslike_c/feelslike_f), and all wind speeds including gusts switch between the API-provided km/h and mph values (wind_kph/wind_mph, gust_kph/gust_mph); pressure stays in mmHg, precipitation stays in mm.
2. **Given** the unit toggle is in metric mode, **When** the toggle button is read, **Then** it displays "°C / °F | km/h / mph" (matching the web UI toggle label).
3. **Given** the unit toggle is in imperial mode, **When** the toggle button is read, **Then** it displays "°F / °C | mph / km/h" (matching the web UI toggle label).
4. **Given** the app is reopened, **When** it loads, **Then** the unit preference persists (stored in DataStore).
5. **Given** widgets are on the home screen, **When** the app unit toggle is changed, **Then** widgets are unaffected (widgets always use metric: °C, km/h, mm as per FR-004/FR-005).

---

### Edge Cases

- What happens when the API rate limit is exceeded (25 requests/minute)? Widget/app should cache last successful response and show stale data indicator.
- How does system handle network timeout? Widget/app shows cached data with timestamp and manual refresh on tap.
- What happens when device is offline? Widget/app displays last cached data with "offline" indicator.
- How does widget handle configuration changes (rotation, theme)? Widget layout adapts to its size.
- What happens when city name contains special characters? API accepts URL-encoded city names; widget/app should handle encoding.
- What happens when the app is backgrounded during animations? Animations pause and resume on foreground.
- What happens when the system "remove animations" setting is enabled? All atmospheric animations are disabled; only static gradients and cards render.
- What happens on low-end devices with many particles? Particle count is capped to maintain 60fps; excess particles are not spawned.

## Requirements

### Functional Requirements

- **FR-001**: System MUST provide an Android home screen widget displaying current weather from GET `/api/v2/weather?city={city}`.
- **FR-002**: System MUST provide an Android home screen widget displaying 3-day forecast from the same endpoint's `forecast.forecastday` array.
- **FR-003**: Widgets and app MUST fetch data from the existing FastAPI endpoint at the configured base URL (e.g., `https://api.weatherender.example/api/v2/weather`).
- **FR-004**: Current weather widget MUST display: temperature (°C), condition text, UV index, humidity (%), pressure (mmHg), snow condition status.
- **FR-005**: Forecast widget MUST display for each of 3 days: date (DD.MM.YYYY), average temperature (°C), precipitation (mm), precipitation chance (%), UV index, wind (km/h), gusts (km/h), snow state. Daily gusts are obtained the same way as the web UI: from the API's `day.gust_kph` if present, otherwise derived as `day.maxwind_kph * 1.2`.
- **FR-006**: Widgets MUST apply condition-based theming matching web UI: sunny, rainy, cloudy, thunder gradients (static colors only, no animations). App screen MUST apply the same gradients PLUS the web UI's atmospheric animated effects. The condition text → theme mapping for BOTH widgets and app uses substring matching on lowercased condition text with precedence (first match wins): thunder (storm, thunder, lightning) → cloudy (cloud, overcast, fog, mist, haze, snow) → rainy (rain, drizzle, shower) → sunny (clear, sunny); default sunny. This mirrors the `data-condition` rules in `src/weatherender/WEB/static/css/style.css`.
- **FR-007**: Widgets and app screen MUST use the same visual component styles as web UI: semi-transparent rounded cards with white borders (no backdrop blur), stat pills, hero temperature display, daily forecast cards.
- **FR-008**: Widgets MUST support city configuration via widget configuration activity with city name input validation (1-100 chars, non-blank).
- **FR-009**: Widgets and app MUST cache last successful API response locally and display it when network is unavailable, with a visual "stale data" indicator showing last update timestamp.
- **FR-010**: Widgets and app MUST respect API rate limits (25 requests/minute) by limiting automatic refreshes to every 30 minutes and supporting manual refresh on tap.
- **FR-011**: Widgets and app MUST handle API error responses (404 city not found, 502 upstream error) gracefully with user-facing error messages.
- **FR-012**: Application MUST be written entirely in Kotlin.
- **FR-013**: App screen MUST provide city input/search functionality with validation (1-100 chars, non-blank).
- **FR-014**: App screen MUST display current weather and 3-day forecast in a single Compose screen using Material 3 components.
- **FR-015**: App screen MUST show loading state during data fetch and error state with retry on failure.
- **FR-016**: App screen atmospheric animations MUST pause when the app is in the background.
- **FR-017**: App screen atmospheric animations MUST be disabled when the system "remove animations" setting (Settings.Global.ANIMATOR_DURATION_SCALE = 0) is enabled.
- **FR-018**: App screen atmospheric animations MUST bound particle counts to maintain smooth performance on low-end devices (no visible stutter).
- **FR-019**: App screen MUST display a 24-hour forecast table with columns: Time, Temperature, Precipitation %, UV Index, Pressure (matching `index.html` table structure). The table shows the next 24 hours starting from the city's current local time (using `location.localtime` / `tz_id` from the API). Pressure is shown in mmHg (computed from `pressure_mb × 0.750062`). Temperature follows the unit toggle; pressure does not.
- **FR-020**: App screen MUST provide a unit toggle switching between metric (°C, km/h) and imperial (°F, mph) for temperature and wind speed ONLY (including gusts) across current weather, hourly table, and daily forecast cards (matching the web UI toggle). The toggle uses the API-provided dual-unit values (temp_c/temp_f, wind_kph/wind_mph, gust_kph/gust_mph, avgtemp_c/avgtemp_f, feelslike_c/feelslike_f); no manual conversion is performed. Pressure (mmHg) and precipitation (mm) are NOT toggled and remain in metric units in all modes.
- **FR-021**: App screen MUST persist unit preference in DataStore and restore on app launch.
- **FR-022**: App screen MUST render two soft-edged (radial-gradient) drifting colored orbs (body::before/::after) on 18s alternate drift cycle with theme-specific colors from `style.css`, a hero-card light orb on 9s cycle (orbFloat), and entrance float-in for content blocks (floatIn, 0.55s ease, staggered delays 0s / 0.08s / 0.12s).
- **FR-023**: Widgets always use metric units (°C, km/h, mm) regardless of app unit toggle setting.

### Key Entities

- **Widget Configuration**: Represents user's selected city and widget preferences (city name, last update timestamp, cached weather response).
- **App Configuration**: Represents user's selected city for the main app screen (city name, last update timestamp, cached weather response).
- **App Preferences**: Represents user's unit preference (metric/imperial) for the app screen.
- **Weather Response**: Current conditions (location, current, snow_state) and forecast array (date, day metrics, snow_forecast) from `/api/v2/weather`.
- **Condition Theme**: Mapping from weather condition text to visual theme (sunny, rainy, cloudy, thunder) determining gradients, static colors, and animated effects.

## Success Criteria

### Measurable Outcomes

- **SC-001**: Widget displays current weather data within 5 seconds of configuration on a typical 4G/WiFi connection.
- **SC-002**: Widget refreshes automatically at least every 30 minutes (Android system managed) and on user tap, without exceeding API rate limits.
- **SC-003**: 95% of widget configurations succeed with valid city names on first attempt.
- **SC-004**: Visual parity achieved: side-by-side comparison shows widget and app screen match web UI color scheme, typography scale, spacing, and semi-transparent card styling for all 4 condition themes.
- **SC-005**: Widget remains functional offline: displays last cached data for up to 24 hours with clear stale indicator.
- **SC-006**: App screen loads and displays weather data within 5 seconds on a typical 4G/WiFi connection.
- **SC-007**: App screen displays cached data offline for up to 24 hours with clear stale indicator.
- **SC-008**: App screen city search succeeds for 95% of valid city names on first attempt.
- **SC-009**: App screen atmospheric animations run without visible stutter on a mid-range device (e.g., Snapdragon 7-series equivalent) and are completely disabled when the system "remove animations" setting is enabled.
- **SC-010**: App screen hourly table displays the next 24 hours from the city's local time with correct columns; unit toggle affects temperature only (pressure stays mmHg).
- **SC-011**: App screen unit toggle switches temperatures (°C↔°F) and wind speeds including gusts (km/h↔mph) instantly using API-provided dual-unit values, persists across app restarts; pressure and precipitation unchanged.

## Assumptions

- The FastAPI `/api/v2/weather` endpoint remains stable and returns the same JSON structure as documented in `WeatherResponseV2` (location, current, forecast, snow_state, snow_forecast, attribution).
- The API base URL is configurable at build time or via a secure configuration mechanism (not hardcoded in widget/app code).
- Application is written entirely in Kotlin.
- No changes to Python backend code are required; existing endpoints serve widget and app needs.
- User has internet connectivity for initial configuration and periodic refreshes.
- WeatherAPI key is managed server-side; widgets/app do not need direct API key access.
- Web UI atmospheric effects reference implementation: `src/weatherender/WEB/static/css/style.css` (gradients, keyframes, data-condition rules, orb/particle colors) and `src/weatherender/WEB/templates/index.html` (DOM structure for weather-scene, weather-hero, blocks) and any JS under `src/weatherender/WEB/static/`.
- The API response includes ~72 hourly entries (3 days × 24 hours) and is approximately 70 KB; it is cached locally per city.
- The city's local time zone from the API (`location.localtime`, `location.tz_id`) drives the hourly forecast window in the app screen.

## Out of Scope

- Python backend changes
- Widget animations (widgets are static: gradients and colors only)
- Cursor/touch-following tilt or parallax effects
- iOS
- Push notifications
- Multiple cities per widget instance or app session
- Hover effects (desktop-only, not applicable to touch)
