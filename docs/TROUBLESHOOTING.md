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

1. Check `docs/[blocked]` and the main README
2. Search existing Issues
3. Open a new issue or contact the maintainer (see SUPPORT.md)

---

### 5. `docs/RELEASING.md`

```markdown
# Releasing Weatherender

## Prerequisites

- You must be a maintainer with push access to `main` and the ability to create tags.
- CI must be green on `main`.
- Version in `pyproject.toml` must match the tag you are about to create.

---

## Release process

1. **Update version**
   Edit `pyproject.toml` → `version = "X.Y.Z"`.

2. **Update changelog**
   Add a new section at the top of `docs/CHANGELOG.md` with the date and notable changes.

3. **Commit**
   ```bash
   git add pyproject.toml docs/CHANGELOG.md
   git commit -m "chore: release vX.Y.Z"
   git push origin main
   ```

4. **Create and push the tag**
   ```bash
   git tag vX.Y.Z
   git push origin vX.Y.Z
   ```

5. **Automated publishing**
   The workflow `.github/workflows/publish-pypi.yml` will:
   - run the full test suite
   - verify that the tag matches `pyproject.toml`
   - build the wheel/sdist
   - publish to PyPI via Trusted Publishing (OIDC)

   GHCR image is published on every push to `main` by `publish-github.yml` (`latest` + commit SHA).

6. **GitHub Release**
   Optionally create a GitHub Release from the tag and paste the changelog section.

---

## Versioning policy

We follow a practical SemVer-inspired scheme:

- **MAJOR** — breaking API or major architectural changes
- **MINOR** — new features (backward-compatible)
- **PATCH** — bug fixes, documentation, dependency updates

Current series: **2.x**

---

## Rollback

If a bad version was published:

1. Yank the version on PyPI if necessary.
2. Revert the commit on `main` (prefer `git revert`, not force-push).
3. Publish a new patch version with the fix.
```

---
