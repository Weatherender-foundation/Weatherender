import json
import logging
import sys

from weatherender.logging_config import JsonFormatter


def test_json_formatter_includes_exception_traceback():
    try:
        raise ValueError("worker failure")
    except ValueError:
        record = logging.LogRecord(
            name="test",
            level=logging.ERROR,
            pathname=__file__,
            lineno=1,
            msg="worker failed",
            args=(),
            exc_info=sys.exc_info(),
        )

    formatted = json.loads(JsonFormatter().format(record))

    assert formatted["message"] == "worker failed"
    assert "ValueError: worker failure" in formatted["exception"]


def test_json_formatter_omits_exception_when_not_present():
    record = logging.LogRecord(
        name="test",
        level=logging.INFO,
        pathname=__file__,
        lineno=1,
        msg="ok",
        args=(),
        exc_info=None,
    )

    formatted = json.loads(JsonFormatter().format(record))

    assert formatted["level"] == "INFO"
    assert formatted["logger"] == "test"
    assert formatted["message"] == "ok"
    assert "exception" not in formatted
