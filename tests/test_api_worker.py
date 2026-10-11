import _queue
from concurrent.futures import ThreadPoolExecutor
from concurrent.futures.thread import queue as executor_queue


def test_gevent_does_not_replace_executor_queue():
    assert executor_queue.SimpleQueue is _queue.SimpleQueue


def test_thread_pool_remains_usable_after_api_import():
    with ThreadPoolExecutor(max_workers=1) as executor:
        assert executor.submit(lambda: "worker ok").result(timeout=1) == "worker ok"
