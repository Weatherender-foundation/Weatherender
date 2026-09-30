from flask import Blueprint, g, request
from marshmallow import ValidationError

from weatherender.models import SessionLocal, WeatherRequest
from weatherender.schemas import CityRequestSchema
from weatherender.services import WeatherService
from weatherender.snow import get_snow_state
from weatherender.WEB.extensions import limiter
from weatherender.WEB.swagger_config import spec

api_bp = Blueprint("api", __name__, url_prefix="/api")

WEATHER_ROUTE_DOC = """Get weather data and snow conditions by city.
---
get:
    parameters:
        - in: query
            name: city
            schema:
                type: string
            required: true
    responses:
        200:
            description: Weather data and snow conditions
        400:
            description: Invalid or missing city
        404:
            description: City not found
        502:
            description: Weather provider unavailable
"""


@api_bp.route("/weather")
@limiter.limit("25 per minute")
def get_weather():
    if "db_session" not in g:
        g.db_session = SessionLocal()
    schema = CityRequestSchema()
    data = request.args.to_dict()
    try:
        load_data = schema.load(data)
    except ValidationError as e:
        error = "Error while fetching city: city must be a string and between 1 and 100 characters. City isn't valid"
        raw_city = data.get("city", "")
        info_valide_err = WeatherRequest(
            city=(
                raw_city
                if (raw_city and len(raw_city) <= 100)
                else (raw_city or "")[:95] + "..."
            ),
            source="api",
            success=0,
            error_message=error,
        )
        g.db_session.add(info_valide_err)
        g.db_session.commit()
        return {"error": e.messages}, 400
    city = load_data["city"]
    weather_data = WeatherService.get_weather(city=city)
    if "error" in weather_data:
        info_err = WeatherRequest(
            city=str(city),
            source="api",
            success=0,
            error_message=weather_data["error"].get("message"),
        )
        g.db_session.add(info_err)
        g.db_session.commit()
        error = weather_data["error"]
        status_code = 404 if error.get("code") == "city_not_found" else 502
        return {"error": {"message": error.get("message")}}, status_code
    else:
        info_suc = WeatherRequest(
            city=city,
            source="api",
            temp_c=round(weather_data["current"]["temp_c"], 2),
            condition=weather_data["current"]["condition"]["text"],
            success=1,
            error_message=None,
        )
        g.db_session.add(info_suc)
        g.db_session.commit()
    forecast_days = weather_data.get("forecast", {}).get("forecastday", [])
    current = weather_data.get("current", {})

    if forecast_days:
        today_day = forecast_days[0].get("day", {})
        weather_data["snow_state"] = get_snow_state(
            temp_c=current.get("temp_c", 0),
            min_temp_c=today_day.get("mintemp_c", current.get("temp_c", 0)),
            max_temp_c=today_day.get("maxtemp_c", current.get("temp_c", 0)),
            humidity=current.get("humidity", 50),
            snow_depth_cm=today_day.get("totalsnow_cm", 0.0),
            snow_24h_cm=today_day.get("totalsnow_cm", 0.0),
            wind_kph=current.get("wind_kph", 0),
            cloud_cover=current.get("cloud", 0),
            condition_text=current.get("condition", {}).get("text", ""),
            prev_day_max_temp=0.0,
            totalprecip_mm=today_day.get("totalprecip_mm", 0.0),
            will_it_snow=today_day.get("daily_will_it_snow", 0),
            totalsnow_cm=today_day.get("totalsnow_cm", 0.0),
        )
    else:
        weather_data["snow_state"] = {"status": "No snow data"}

    snow_forecast = []
    for i, day in enumerate(forecast_days):
        day_info = day.get("day", {})
        if i > 0:
            prev_day_info = forecast_days[i - 1].get("day", {})
            prev_day_max_temp = prev_day_info.get(
                "maxtemp_c", day_info.get("avgtemp_c", 0)
            )
        else:
            prev_day_max_temp = 0.0

        day_snow_state = get_snow_state(
            temp_c=day_info.get("avgtemp_c", 0),
            min_temp_c=day_info.get("mintemp_c", 0),
            max_temp_c=day_info.get("maxtemp_c", 0),
            humidity=day_info.get("avghumidity", 50),
            snow_depth_cm=day_info.get("totalsnow_cm", 0.0),
            snow_24h_cm=day_info.get("totalsnow_cm", 0.0),
            wind_kph=day_info.get("maxwind_kph", 0),
            cloud_cover=50,
            condition_text=day_info.get("condition", {}).get("text", ""),
            prev_day_max_temp=prev_day_max_temp,
            totalprecip_mm=day_info.get("totalprecip_mm", 0.0),
            will_it_snow=day_info.get("daily_will_it_snow", 0),
            totalsnow_cm=day_info.get("totalsnow_cm", 0.0),
        )
        snow_forecast.append(
            {
                "date": day.get("date"),
                "snow_state": day_snow_state,
            }
        )

    weather_data["snow_forecast"] = snow_forecast
    return weather_data, 200


get_weather.__doc__ = WEATHER_ROUTE_DOC


@api_bp.route("/apispec.json")
@limiter.limit("25 per minute")
def get_apispec():
    """Serialize and return the APISpec OpenAPI documentation schema as JSON."""
    return spec.to_dict()


@api_bp.route("/ping", methods=["GET", "HEAD"])
def ping() -> tuple[dict[str, str], int]:
    """Perform a simple API health check, returning status 'ok' and 200 OK."""
    return {"status": "ok"}, 200
