import json
import logging
from typing import Any

import redis
from redis.exceptions import RedisError

from weatherender.config import Config

logger = logging.getLogger(__name__)


class CacheService:
    def __init__(self, prefix: str = "weatherender:") -> None:
        """Initialize the CacheService with a Redis client constructed from Config.REDIS_URL."""
        self.prefix = prefix
        self.client: redis.Redis = redis.from_url(
            Config.REDIS_URL,
            decode_responses=True,
        )

    def _make_key(self, key: str) -> str:
        """Formulate a prefixed key to prevent collisions."""
        return f"{self.prefix}{key}" if self.prefix and not key.startswith(self.prefix) else key

    def get(self, key: str) -> Any | None:
        """Retrieve and JSON-decode the cached value associated with the specified key."""
        full_key = self._make_key(key)
        try:
            value = self.client.get(full_key)
            if value is not None:
                return json.loads(value)
        except (RedisError, json.JSONDecodeError) as err:
            logger.warning("Failed to get key '%s' from cache: