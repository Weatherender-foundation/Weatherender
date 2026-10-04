from weatherender.banner import print_startup_banner


def on_starting(_server: object) -> None:
    print_startup_banner()
