import _queue
from concurrent.futures.thread import queue as executor_queue


def test_gevent_does_not_replace_executor_queue():
    assert executor_queue.SimpleQueue is _queue.SimpleQueue
