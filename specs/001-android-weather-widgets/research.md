# Research Findings: Web Visual Effects & Logic Parity

## Real Web Visual Effects (from `style.css`)

### Atmospheric Effects & Animations

| Effect | CSS Selector | Animation | Timing | Keyframes |
|--------|--------------|-----------|--------|-----------|
| **Sun glow** | `.sun-glow` | `opacity` transition | 0.4s ease | radial-gradient circle, scale(0.8)→scale(1) |
| **Particles** | `.particles` | `particleDrift` | 12s linear infinite | `::before` + `::after` (4s delay) |
| **Clouds** | `.cloud` | `floatCloud` | 18s ease-in-out infinite alternate | 2 clouds (`.cloud-one`, `.cloud-two` with 2s delay) |
| **Rain** | `.rain` | `rainFall` | 1.2s linear infinite | 3 drops (0s, 0.2s, 0.4s delays) |
| **Lightning** | `.lightning` | `lightningFlash` | 6s ease-in-out infinite | Opacity: 0→1→0.2→1→0 at 0%/21%/22%/23%/100% |
| **Stars** | `.stars` | `twinkle` | 4s ease-in-out infinite alternate | Opacity 0.2→1, scale 0.8→1.2 |
| **Orbs (ambient)** | `.weather-hero::before` | `orbFloat` | 9s ease-in-out infinite alternate | translate3d(0,0,0)→translate3d(45px,20px,0) scale(1.1) |
| **Float-in cards** | `.block-current`, `.block-hourly`, `.block-daily` | `floatIn` | 0.55s ease | 0%: opacity 0, translateY(12px) scale(0.98) → 100%: opacity 1, translateY(0) scale(1) |
| **Body drift** | `body::before`, `body::after` | `drift` | 18s ease-in-out infinite alternate | translate3d(0,0,0)→translate3d(26px,30px,0) scale(1.12) |

### Condition-Based Backgrounds (lines 109-175)

| Condition Class | Background Gradient |
|-----------------|---------------------|
| `sunny` / `clear` | `linear-gradient(135deg, #4facfe, #00f2fe)` |
| `rainy` / `rain` / `drizzle` / `shower` | `linear-gradient(135deg, #0f2027, #203a43 35%, #2c5364)` |
| `cloudy` / `cloud` / `overcast` / `fog` / `mist` / `haze` / `snow` | `linear-gradient(135deg, #4b5d7d, #b5c7db 52%, #edf2ff)` |
| `thunder` / `storm` / `thunder` / `lightning` | `linear-gradient(135deg, #0a1220, #1e2a39 32%, #2c3e50)` |

### Pseudo-element Colors per Condition (lines 136-175)

| Condition | `::before` | `::after` |
|-----------|------------|-----------|
| sunny | `rgba(255, 212, 87, 0.38)` | `rgba(142, 224, 255, 0.44)` |
| rainy | `rgba(95, 176, 255, 0.35)` | `rgba(169, 223, 255, 0.3)` |
| cloudy | `rgba(255, 255, 255, 0.45)` | `rgba(164, 181, 217, 0.38)` |
| thunder | `rgba(117, 130, 255, 0.28)` | `rgba(140, 168, 255, 0.2)` |

### Reduced Motion (lines 834-841)
```css
@media (prefers-reduced-motion: reduce) {
    *, *::before, *::after {
        animation-duration: 0.001ms !important;
        animation-iteration-count: 1 !important;
        transition-duration: 0.001ms !important;
    }
}
```

---

## Hourly Table: 24-Hour Selection Logic (from `app.py` lines 252-281)

```python
location_data = data.get("location", {})
raw_localtime = location_data.get("localtime")  # e.g., "2026-10-19 14:30"
current_hour_str = ""
if raw_localtime:
    api_localtime = datetime.strptime(raw_localtime, "%Y-%m-%d %H:%M").replace(tzinfo=UTC)
    current_hour_str = api_localtime.strftime("%Y-%m-%d %H:00")  # "2026-10-19 14:00"

all_hours = [hour for day in forecast_days for hour in day.get("hour", [])]
count = 0
for hour in all_hours:
    if hour.get("time", "") >= current_hour_str:  # string comparison works for ISO format
        prob_precip = max(hour.get("chance_of_rain", 0), hour.get("chance_of_snow", 0))
        hourly_forecast.append({
            "time": hour["time"].split(" ")[1],      # "14:00"
            "temp": round(hour["temp_c"], 2),
            "precipitation": prob_precip,
            "uv": round(hour["uv"]),
            "pressure": round(hour["pressure_mb"] * 0.750062),  # mb → mmHg
        })
        count += 1
        if count == 24:
            break
```

**Key points:**
- Uses city's local time from `location.localtime` (not UTC)
- Flattens all forecast days' hours into single list
- Selects hours >= current hour, takes exactly 24 entries
- Pressure conversion: `pressure_mb * 0.750062` → mmHg
- Precipitation % = max(chance_of_rain, chance_of_snow)

---

## Daily Gusts Fallback (from `app.py` lines 334-336)

```python
"gust": round(
    day_info.get("gust_kph", day_info.get("maxwind_kph", 0.0) * 1.2)
),
```

**Rule:** If `gust_kph` is missing from API, compute `maxwind_kph * 1.2` and round.

---

## Unit Toggle Labels (from `units.js` lines 24-26)

```javascript
toggleBtn.textContent = isMetric
    ? '°C / °F | km/h / mph'      // Metric mode: shows imperial as alternative
    : '°F / °C | mph / km/h';     // Imperial mode: shows metric as alternative
```

**Exact labels:**
- Metric: `°C / °F | km/h / mph`
- Imperial: `°F / °C | mph / km/h`

**Note:** Web converts via math (temp_c → temp_f, kmh → mph). Android must use API fields directly (`temp_f`, `avgtemp_f`, `wind_mph`, `gust_mph`) per spec.
