# Third-Party Software and Services

Weatherender depends on third-party software and services. This document identifies the direct Python dependencies declared in [`requirements.txt`](requirements.txt) and the external services referenced by the application. Each third party remains responsible for its own software, content, terms, trademarks, and service availability.

This notice is informational and does not replace the license or notices distributed by any dependency. Before redistributing Weatherender, verify the license and notice files for the exact versions installed, including transitive dependencies.

## Project license boundary

The Weatherender source code, SSCI algorithm, documentation, and other project materials are distributed under the [Apache License 2.0](LICENSE), unless a file or component states otherwise. That license does not relicense third-party software and does not remove rights granted by a dependency's own license. The most restrictive applicable terms must be respected for each component.

## Direct Python dependencies

The versions below are the direct pins currently declared in `requirements.txt`. License names are a convenience summary; the authoritative terms are the license files and metadata published by each project.

| Component | Pinned version | License reference |
|---|---:|---|
| Flask, Gunicorn, gevent, psycogreen | 3.0.3, 22.0.0, 26.8.0, 1.0.2 | [PyPI](https://pypi.org/) package metadata |
| flask-limiter, flask-swagger-ui, flask-talisman | 4.1.1, 5.32.9, 1.1.0 | [PyPI](https://pypi.org/) package metadata |
| prometheus-flask-exporter, apispec, apispec-webframeworks | 0.23.2, 6.10.0, 1.2.0 | [PyPI](https://pypi.org/) package metadata |
| FastAPI, Uvicorn, SlowAPI | 0.141.1, 0.52.4, 0.1.10 | [PyPI](https://pypi.org/) package metadata |
| requests, httpx, a2wsgi | 2.32.3, 0.28.1, 1.10.10 | [PyPI](https://pypi.org/) package metadata |
| build, twine | 1.6.0, 7.0.0 | [PyPI](https://pypi.org/) package metadata |
| SQLAlchemy, psycopg2-binary, Alembic, asyncpg | 2.0.35, 2.9.12, 1.13.3, 0.31.0 | [PyPI](https://pypi.org/) package metadata |
| redis, types-redis | 8.1.0, 4.6.0.20241004 | [PyPI](https://pypi.org/) package metadata |
| Marshmallow, python-dotenv, python-dateutil, types-python-dateutil | 3.22.0, 1.0.1, 2.9.0, 2.9.0.20260716 | [PyPI](https://pypi.org/) package metadata |
| APScheduler | 3.11.3 | [PyPI](https://pypi.org/) package metadata |
| pytest, pytest-asyncio, pytest-cov | 9.1.1, 1.4.0, 7.1.0 | [PyPI](https://pypi.org/) package metadata |
| pre-commit, mypy, types-requests, respx, tenacity | 4.6.0, 2.3.0, 2.33.0.20260712, 0.23.1, 9.1.4 | [PyPI](https://pypi.org/) package metadata |

The dependency list includes both runtime and development/build tools. Some packages bring additional transitive dependencies that are not enumerated here.

## External services and data sources

Weatherender can communicate with the following third-party services, depending on deployment configuration:

| Service | Use in Weatherender | Responsibility |
|---|---|---|
| [WeatherAPI](https://www.weatherapi.com/) | Current weather and forecast data | Users and operators must provide a valid API key and follow WeatherAPI terms, quotas, and attribution requirements. |
| [ip-api.com](https://ip-api.com/) | Primary IP-based location lookup | Availability, accuracy, rate limits, and terms are controlled by the provider. |
| [ipinfo.io](https://ipinfo.io/) | Fallback IP-based location lookup | Availability, accuracy, rate limits, and terms are controlled by the provider. |
| PostgreSQL | Persistent request history and application storage | The database server and hosted instance are operated under their own terms. |
| Redis / [Upstash](https://upstash.com/) | TTL weather-response caching | Cache availability, retention, limits, and hosted-service terms apply. |
| [Render](https://render.com/) | Example production hosting | Hosting availability and account terms apply to the deployment operator. |
| [Supabase](https://supabase.com/) | Example hosted PostgreSQL production storage | Hosted database terms, quotas, and privacy obligations apply to the operator. |
| [UptimeRobot](https://uptimerobot.com/) | Example availability monitoring for `/api/ping` | Monitoring requests and account terms apply to the operator. |
| GitHub Actions, GHCR, PyPI, Codecov, and k6 | CI/CD, image/package distribution, coverage, and load testing | Their respective terms, quotas, and service policies apply when used. |

Weatherender does not grant permission to use any third-party trademarks or service beyond the relevant provider's terms. Operators are responsible for API keys, privacy notices, data-protection obligations, quotas, and any required attribution.

## Updating this notice

When adding or removing a direct dependency or external integration:

1. Update `requirements.txt` or the relevant deployment configuration.
2. Check the new component's license and notice requirements.
3. Update this file with the exact pinned version and purpose.
4. Run the project's normal tests and CI checks before merging.

Questions about third-party licensing should be sent to [Alexey Lyapin](mailto:lehacomp16@gmail.com). This project cannot provide legal advice.
