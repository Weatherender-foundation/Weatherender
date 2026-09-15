# Roadmap

This document tracks planned and completed major features of **Weatherender**.

Status legend:
- [x] Completed
- [ ] Planned / In progress
- [~] Partially done / Under consideration

---

## Completed

- [x] **Asynchronous API v2 (FastAPI)**
  High-performance `/api/v2/*` stack with Pydantic v2, `httpx`, `asyncpg`, Redis async client, and rate limiting via `slowapi`. Mounted alongside the Flask app via `a2wsgi.WSGIMiddleware`.

- [x] **Python package & GHCR image**
  Installable package on PyPI (`pip install weatherender`) and production Docker image on GitHub Container Registry (`ghcr.io/weatherender-foundation/weatherender-api`).

- [x] **Snow Surface Condition Index (SSCI)**
  Proprietary algorithm that classifies snow quality for alpine sports (see [SSCI.md](SSCI.md)).

- [x] **Production deployment**
  Render + Supabase (PostgreSQL) + Upstash (Redis) + UptimeRobot keep-alive.

- [x] **Full test & CI suite**
  Pytest coverage, Ruff, Mypy, Codecov, k6 load testing, and automated PyPI publishing via Trusted Publishing.

---

## Planned

- [ ] **Dynamic Radar Maps**
  Interactive precipitation radar and weather maps (Leaflet / GIS) to visualize snow and rain fronts for ski resorts.

- [ ] **User Authentication & Custom Alerts**
  JWT / session-based auth (likely via Supabase Auth). Users will be able to save favourite resorts and configure personal snow/weather alerts.

- [ ] **Improved SSCI**
  More granular statuses, historical trend analysis, and optional resort-specific calibration.

- [ ] **CLI enhancements**
  Better report formatting, export to JSON/CSV, and optional Telegram/Email delivery of forecasts.

---

## Under consideration

- [~] Public status page / better free-tier reliability monitoring
- [~] Optional self-hosted weather data sources (to reduce dependency on WeatherAPI.com)
- [~] GraphQL endpoint (in addition to REST)

---

Last updated: September 2026
