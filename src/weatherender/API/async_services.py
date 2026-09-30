import logging

import httpx
from tenacity import (
    retry,
    retry_if_exception_type,
    stop_after_attempt,
    wait_exponential,
)

from weatherender.API.async_cache import AsyncCacheService
from weatherender.config import Config

logger = logging.getLogger(__name__)
cache_service = AsyncCacheService()


@retry(
    stop=stop_after_attempt(3),
    wait=wait_exponential(multiplier=1, min=0.2, max=2.0),
    retry=retry_if_exception_type((httpx.RequestError, httpx.HTTPStatusError)),
    reraise=True,
)
async def _request_weather(
    client: httpx.AsyncClient, params: dict[str, str | int]
) -> httpx.Response:
    response = await client.get(Config.WEATHER_URL, params=params, timeout=5)
    if response.status_code >= 500:
        response.raise_for_status()
    return response


class AsyncWeatherService:
    @staticmethod
    async def get_weather_async(
        client: httpx.AsyncClient, city: str, api_key: str | None = None
    ) -> dict:
        """Asynchronously fetch 3-day forecast and current weather details for a city or coordinates.

        Leverages an async Redis client for response caching and an async HTTP client for requests.
        """
        if isinstance(city, tuple):
            city = f"{city[0]},{city[1]}"
        else:
            city = city.strip()
        active_key = api_key or getattr(Config, "WEATHER_API_KEY", None)
        if not active_key:
            return {
                "error": {
                    "message": "API key is missing. Please provide a valid WeatherAPI key.",
                    "code": "upstream_error",
                }
            }
        cache_key = f"weather:{city.strip().lower()}"
        cached_data = await cache_service.get(cache_key)
        if cached_data:
            return cached_data
        params = {
            "key": active_key,
            "q": city,
            "days": 3,
            "aqi": "yes",
            "alerts": "no",
            "lang": "en",
        }
        try:
            response = await _request_weather(client, params)
            if response.status_code in [401, 403]:
                return {
                    "error": {
                        "message": "Invalid API key. Please check your key and try again.",
                        "code": "upstream_error",
                    }
                }
            if response.status_code == 400:
                return {
                    "error": {
                        "message": f"City '{city}' not found.",
                        "code": "city_not_found",
                    }
                }
            if "application/json" not in response.headers.get("Content-Type", ""):
                return {
                    "error": {
                        "message": f"API returned invalid response format (Status: {response.status_code}). "
                        f"Perhaps access is blocked. Please enable or change your VPN location!",
                        "code": "upstream_error",
                    }
                }
            if response.status_code != 200:
                return {
                    "error": {
                        "message": f"Weather service error. Status code: {response.status_code}",
                        "code": "upstream_error",
                    }
                }
            try:
                data = response.json()
                await cache_service.set(cache_key, data)
                return data
            except ValueError:
                return {
                    "error": {
                        "message": "Error parsing response from server. Please check your internet connection.",
                        "code": "upstream_error",
                    }
                }
        except (httpx.RequestError, httpx.HTTPStatusError) as e:
            logger.error(f"Network error while fetching weather for {city}: {e}")
            return {
                "error": {
                    "message": "Network error. Look up your internet connection or try again later.",
                    "code": "upstream_unavailable",
                }
            }
