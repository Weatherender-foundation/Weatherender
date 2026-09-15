# Troubleshooting

## Local development

### `docker compose up` fails / containers exit immediately
- Check that ports 5001, 8001, 5432, 6379, 5433 are free.
- Make sure `.env` exists and contains at least `WEATHER_API_KEY` and `SECRET_KEY`.
- Run `docker compose logs` to see the exact error.

### Alembic / database errors
```bash
docker compose up -d weather_db
docker compose run --rm cli alembic upgrade head
```

If the test database is involved, also start `weather_test_db` (port 5433).

`ModuleNotFoundError: weatherender`

You are probably running outside the package context. Prefer:

```bash
docker compose run --rm cli weatherender
# or
pip install -e .
weatherender
```

## Redis connection refused

The app continues to work (graceful fallback). To fix Redis:

```bash
docker compose up -d cache
```

---

# Production / Demo

## Cold starts (30–50 s)

Normal on Render free tier. UptimeRobot keeps the service warmer, but occasional spin-downs still occur.

## Database paused (Supabase)

Free-tier Supabase projects pause after 7 days of inactivity. Open a GitHub issue asking for a wake-up.

## 429 Too Many Requests

You hit the rate limit (25 requests/minute on the weather endpoints). Wait a minute or reduce concurrency.

## `/health` or `/api/v2/health` returns 503

Database connectivity problem. Check `DATABASE_URL` (must use Supabase **Session pooler**, not Direct or Transaction pooler).

## Migrations not applied after deploy

Render free tier does not support Pre-Deploy Commands. Run migrations manually:

```bash
DATABASE_URL="<supabase-session-pooler-url>" alembic upgrade head
```

---

# CLI

## CLI requires DATABASE_URL even for a simple query

Yes — every run logs a `WeatherRequest` row (success or failure). Provide a reachable PostgreSQL instance.

## Report file not appearing

Inside Docker the report is written to `src/weatherender/CLI/weather_report.txt`. Make sure the volume mount is present (`./src:/app/src`).

---

# Still stuck?

1. Check required programs and dependencies and the main README
2. Search existing Issues
3. Open a new issue or contact the maintainer (see SUPPORT.md)

---
