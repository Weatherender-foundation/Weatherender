# Feature Specification: Android Weather Widgets

**Feature Branch**: `feature/android`

**Created**: 2026-10-18

**Status**: Approved

**Input**: User description: "Android app in ./android (Kotlin only) with static widgets showing current weather and forecast from the existing Flask/FastAPI endpoints, in the same visual style as the web UI. No changes to the Python code."

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

### User Story 3 - Visual Style Parity with Web UI (Priority: P2)

As an Android user, I want the widgets to match the web UI's visual design language so that the experience feels cohesive across platforms.

**Why this priority**: Brand consistency and user trust; the web UI has a distinctive atmospheric design that should translate to widgets.

**Independent Test**: Can be verified by side-by-side visual comparison of widget screenshots and web UI screenshots for the same weather conditions.

**Acceptance Scenarios**:

1. **Given** a "sunny" condition, **When** the widget renders, **Then** it uses the sunny gradient background (#4facfe to #00f2fe).
2. **Given** a "rainy" condition, **When** the widget renders, **Then** it uses the rainy gradient (#0f2027 to #2c5364).
3. **Given** a "cloudy" condition, **When** the widget renders, **Then** it uses the cloudy gradient (#4b5d7d to #edf2ff).
4. **Given** a "thunder" condition, **When** the widget renders, **Then** it uses the thunder gradient (#0a1220 to #2c3e50).
5. **Given** any condition, **When** the widget renders, **Then** it uses the same card styling: semi-transparent dark cards with white borders, rounded corners, and a typography scale matching the web UI.

---

### User Story 4 - Widget Configuration (Priority: P2)

As an Android user, I want to configure the widget's target city through a configuration activity so that I can see weather for my location of choice.

**Why this priority**: Widgets must be configurable per Android platform requirements; users need to set their city.

**Independent Test**: Can be tested by long-pressing widget → configure → entering city name → saving → widget updates to show new city.

**Acceptance Scenarios**:

1. **Given** the user adds the widget, **When** they tap the configuration area, **Then** a configuration activity opens allowing city name input.
2. **Given** a valid city is entered and saved, **When** the widget refreshes, **Then** it fetches and displays weather for that city.
3. **Given** an invalid city is entered, **When** saved, **Then** the configuration shows an error and does not save.

---

### Edge Cases

- What happens when the API rate limit is exceeded (25 requests/minute)? Widget should cache last successful response and show stale data indicator.
- How does system handle network timeout? Widget shows cached data with timestamp and manual refresh on tap.
- What happens when device is offline? Widget displays last cached data with "offline" indicator.
- How does widget handle configuration changes (rotation, theme)? Widget layout adapts to its size.
- What happens when city name contains special characters? API accepts URL-encoded city names; widget should handle encoding.

## Requirements

### Functional Requirements

- **FR-001**: System MUST provide an Android home screen widget displaying current weather from GET `/api/v2/weather?city={city}`.
- **FR-002**: System MUST provide an Android home screen widget displaying 3-day forecast from the same endpoint's `forecast.forecastday` array.
- **FR-003**: Widgets MUST fetch data from the existing FastAPI endpoint at the configured base URL (e.g., `https://api.weatherender.example/api/v2/weather`).
- **FR-004**: Current weather widget MUST display: temperature (°C), condition text, UV index, humidity (%), pressure (mmHg), snow condition status.
- **FR-005**: Forecast widget MUST display for each of 3 days: date (DD.MM.YYYY), average temperature (°C), precipitation (mm), precipitation chance (%), UV index, wind (km/h), gusts (km/h), snow state.
- **FR-006**: Widgets MUST apply condition-based theming matching web UI: sunny, rainy, cloudy, thunder gradients (static colors only, no animations).
- **FR-007**: Widgets MUST use the same visual component styles as web UI: semi-transparent rounded cards with white borders (no backdrop blur), stat pills, hero temperature display, daily forecast cards.
- **FR-008**: Widgets MUST support city configuration via widget configuration activity with city name input validation (1-100 chars, non-blank).
- **FR-009**: Widgets MUST cache last successful API response locally and display it when network is unavailable, with a visual "stale data" indicator showing last update timestamp.
- **FR-010**: Widgets MUST respect API rate limits (25 requests/minute) by limiting automatic refreshes to every 30 minutes and supporting manual refresh on tap.
- **FR-011**: Widgets MUST handle API error responses (404 city not found, 502 upstream error) gracefully with user-facing error messages.
- **FR-012**: Application MUST be written entirely in Kotlin.

### Key Entities

- **Widget Configuration**: Represents user's selected city and widget preferences (city name, last update timestamp, cached weather response).
- **Weather Response**: Current conditions (location, current, snow_state) and forecast array (date, day metrics, snow_forecast) from `/api/v2/weather`.
- **Condition Theme**: Mapping from weather condition text to visual theme (sunny, rainy, cloudy, thunder) determining gradients and static colors.

## Success Criteria

### Measurable Outcomes

- **SC-001**: Widget displays current weather data within 5 seconds of configuration on a typical 4G/WiFi connection.
- **SC-002**: Widget refreshes automatically at least every 30 minutes (Android system managed) and on user tap, without exceeding API rate limits.
- **SC-003**: 95% of widget configurations succeed with valid city names on first attempt.
- **SC-004**: Visual parity achieved: side-by-side comparison shows widget matches web UI color scheme, typography scale, spacing, and semi-transparent card styling for all 4 condition themes.
- **SC-005**: Widget remains functional offline: displays last cached data for up to 24 hours with clear stale indicator.

## Assumptions

- The FastAPI `/api/v2/weather` endpoint remains stable and returns the same JSON structure as documented in `WeatherResponseV2` (location, current, forecast, snow_state, snow_forecast, attribution).
- The API base URL is configurable at build time or via a secure configuration mechanism (not hardcoded in widget code).
- Application is written entirely in Kotlin.
- No changes to Python backend code are required; existing endpoints serve widget needs.
- User has internet connectivity for initial configuration and periodic refreshes.
- WeatherAPI key is managed server-side; widgets do not need direct API key access.

## Out of Scope

- Python backend changes
- Animations, particle effects, lightning, or cloud motion (widgets are static)
- Full app screen/activity beyond widget configuration
- Push notifications
- Multiple cities per widget instance
