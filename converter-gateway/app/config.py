from os import getenv


class Config:
    RABBIT_HOST = getenv('RABBIT_HOST', '172.18.42.2')
    RABBIT_USER = getenv('RABBIT_USER', 'rabbit')
    RABBIT_PASSWORD = getenv('RABBIT_PASSWORD', 'rabbit123')

    RABBIT_URL = "amqp://" + RABBIT_USER + ":" + RABBIT_PASSWORD + "@" + RABBIT_HOST

    RATINGS_ADDED_EXCHANGE_NAME = "rating.added.exchange"
    TAGS_ADDED_EXCHANGE_NAME = "tags.added.exchange"
    ROUTING_KEY = ""
