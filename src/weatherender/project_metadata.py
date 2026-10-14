from typing import Final, TypedDict


class ProjectAttribution(TypedDict):
    copyright: str
    author: str
    license: str
    license_url: str
    trademarks_url: str


PROJECT_ATTRIBUTION: Final[ProjectAttribution] = {
    "copyright": "© 2026 Weatherender Foundation",
    "author": "Alexey Lyapin",
    "license": "GNU Affero General Public License v3.0",
    "license_url": "https://github.com/Weatherender-foundation/Weatherender/blob/main/LICENSE",
    "trademarks_url": "https://github.com/Weatherender-foundation/Weatherender/blob/main/TRADEMARKS.md",
}
