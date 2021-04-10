import json

from aio_pika import Message, DeliveryMode, ExchangeType, connect_robust
from uuid import uuid1
import logging

from config import Config


async def send_rating_added_message(loop, movie_id, rating):
    connection = await connect_robust(
        Config.RABBIT_URL, loop=loop
    )

    channel = await connection.channel()

    ratings_added_exchange = await channel.declare_exchange(
        Config.RATINGS_ADDED_EXCHANGE_NAME,
        ExchangeType.FANOUT,
        durable="true"
    )

    rating_added_message = {
        "movieId": movie_id,
        "rating": rating
    }

    correlation_id = uuid1()

    message = Message(json.dumps(rating_added_message).encode('utf-8'),
                      delivery_mode=DeliveryMode.PERSISTENT,
                      correlation_id=correlation_id)

    await ratings_added_exchange.publish(
        message, Config.ROUTING_KEY
    )

    logging.info("Message sent to add rating:" + rating + " to movieId:" + movie_id +
          " with correlationId:" + str(correlation_id))

