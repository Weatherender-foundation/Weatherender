from art import text2art


def print_startup_banner() -> None:
    print(text2art("Weatherender", font="slant"), flush=True)
