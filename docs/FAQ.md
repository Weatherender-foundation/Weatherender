# Frequently Asked Questions

## General

**What is Weatherender?**
A production-grade weather application with a web UI, CLI, and dual REST API (Flask v1 + FastAPI v2).

**Is it free?**
Yes. Weatherender is available under the [Apache License 2.0](../LICENSE),
including for commercial use under its terms.

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
