from unittest.mock import MagicMock, patch

import pytest

from weatherender.banner import print_startup_banner
from weatherender.WEB.gunicorn_config import on_starting


def test_print_startup_banner(capsys: pytest.CaptureFixture[str]) -> None:
    with patch("weatherender.banner.text2art", return_value="banner") as text2art:
        print_startup_banner()

    text2art.assert_called_once_with("Weatherender", font="slant")
    assert capsys.readouterr().out == "banner\n"


def test_gunicorn_prints_startup_banner_once() -> None:
    with patch("weatherender.WEB.gunicorn_config.print_startup_banner") as print_banner:
        on_starting(MagicMock())

    print_banner.assert_called_once_with()
