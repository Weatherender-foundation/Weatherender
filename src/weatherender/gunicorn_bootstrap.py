from art import text2art
from gevent import monkey

monkey.patch_ssl()

def on_starting(server: object) -> None:
    print(text2art("Weatherender", font="slant"), flush=True)
