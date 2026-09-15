# Snow Surface Condition Index (SSCI)

**SSCI** is Weatherender’s proprietary algorithm that turns raw meteorological data into actionable snow-quality labels for alpine skiers and snowboarders.

Implementation: [`src/weatherender/snow.py`](../src/weatherender/snow.py) → function `get_snow_state()`.

---

## Input parameters

| Parameter              | Type   | Description                                      |
|------------------------|--------|--------------------------------------------------|
| `temp_c`               | float  | Current temperature (°C)                         |
| `min_temp_c`           | float  | Today’s minimum temperature                      |
| `max_temp_c`           | float  | Today’s maximum temperature                      |
| `humidity`             | int    | Relative humidity (%)                            |
| `snow_depth_cm`        | float  | Current snow depth on the ground                 |
| `snow_24h_cm`          | float  | Snowfall in the last 24 hours                    |
| `wind_kph`             | float  | Wind speed (km/h)                                |
| `cloud_cover`          | int    | Cloud cover (%)                                  |
| `condition_text`       | str    | Text description from WeatherAPI                 |
| `prev_day_max_temp`    | float  | Previous day’s maximum temperature               |
| `totalprecip_mm`       | float  | Total precipitation (mm)                         |
| `will_it_snow`         | int    | Forecast flag (0/1)                              |
| `totalsnow_cm`         | float  | Total snow amount in the forecast period         |

Derived value:
```python
snow_density = (totalprecip_mm / (snow_24h_cm * 10)) if snow_24h_cm > 0 else 0.1
```

## Decision logic (simplified)

1. **No snow**
   `temp_c > 15` or no snow present and no snow expected.

2. **Ice crust**
   Freeze-thaw cycle (`max > 0` and `min < 0`), or previous day was warm (`prev_day_max_temp > 2`), or condition text contains "ice"/"freezing", **and** recent snowfall < 5 cm.

3. **Spring slush / Wet snow**
   Temperature around or above 0 °C combined with high humidity. Clear skies + positive temperature → "Spring slush".

4. **Powder family** (cold conditions)
   * `temp_c <= -5` and ≥ 15 cm of new snow:
     * very low density → **Dry champagne powder!**
     * high wind → **Wind-drifted powder**
     * otherwise → **Powder**

5. **Wind slab**
   Strong wind (`> 35 km/h`) regardless of exact temperature (when snow is present).

6. **Firm / Hard-packed**
   Cold temperatures with moderate recent snowfall → "Fresh, firm snow" or "Hard-packed snow".

7. **Fallback**
   Everything else → **Unstable conditions**.

---

## Possible status values

| Status | Meaning / Typical conditions |
| :--- | :--- |
| `No snow` | No snow cover or too warm |
| `Ice crust` | Freeze-thaw or refrozen surface |
| `Spring slush` | Warm, sunny, wet snow |
| `Wet snow` | Temperature near 0 °C, high humidity |
| `Dry champagne powder!` | Very cold + light density new snow |
| `Wind-drifted powder` | Cold powder moved by strong wind |
| `Powder` | Classic cold powder |
| `Wind slab` | Wind-packed snow (potentially dangerous) |
| `Fresh, firm snow` | Recent cold snowfall, denser |
| `Hard-packed snow` | Older, compressed snow |
| `Unstable conditions` | Fallback / mixed / unclear |

---

## Usage in the API

Both `/api/weather` (v1) and `/api/v2/weather` return a `snow_state` object containing at least:

```json
{
  "status": "Dry champagne powder!"
}
```

The algorithm is pure Python, has no external dependencies, and runs in both the synchronous Flask path and the asynchronous FastAPI path.

---

# Frequently Asked Questions (`docs/FAQ.md`)

## General

**What is Weatherender?**
A production-grade weather application with a web UI, CLI, and dual REST API (Flask v1 + FastAPI v2).

**Is it free?**
Yes for non-commercial use. See the [SSCI Custom License](../LICENSE). Commercial use requires a license.

**Who maintains the project?**
Alexey Lyapin ([@LyapinAlexey](https://github.com/LyapinAlexey)). See [MAINTAINERS.md](../MAINTAINERS.md).

---

## Live Demo

**Why is the first request sometimes slow (30–50 s)?**
The demo runs on Render’s free tier. Containers spin down after ~15 minutes of inactivity.

**The demo says the database is paused. What should I do?**
Supabase free tier pauses projects after 7 days of zero traffic. Open an issue and the maintainer will resume it.

**Is the demo rate-limited?**
Yes. `/api/v2/weather` is limited to 25 requests per minute per IP. `/api/weather` has similar limits.

---

## Installation & Local Development

**What Python version do I need?**
Python 3.13+.

**Do I need a WeatherAPI key?**
Yes, a free key from [weatherapi.com](https://www.weatherapi.com/) (`WEATHER_API_KEY`).

| Service / Component | Port |
| :--- | :--- |
| Flask only | 5001 |
| FastAPI + Flask | 8001 |
| PostgreSQL | 5432 |
| Redis | 6379 |
| Test PostgreSQL | 5433 |

---

## API

**Difference between v1 and v2?**
- v1 (`/api/weather`) – synchronous Flask + Marshmallow
- v2 (`/api/v2/weather`) – asynchronous FastAPI + Pydantic v2 + `httpx`

Both return SSCI data. Production serves both from the same process.

**Where are the interactive docs?**
- v2: `/v2/docs` (Swagger) and `/v2/redoc`
- v1: `/apidocs`

---

## Caching & Performance

**How long are responses cached?**
5 minutes by default (`REDIS_TTL=300`). If Redis is down, the app falls back to a live WeatherAPI call.

**Why do I sometimes get 429?**
Rate limiting is intentional (25 req/min on the main weather endpoints).
