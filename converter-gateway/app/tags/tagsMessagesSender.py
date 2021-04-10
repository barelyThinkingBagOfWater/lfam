import json

from aio_pika import Message, DeliveryMode, ExchangeType, connect_robust
from uuid import uuid1
import logging

from config import Config

async def send_tags_added_message(loop, movie_id, tag):
    connection = await connect_robust(
        Config.RABBIT_URL, loop=loop
    )

    channel = await connection.channel()

    tags_added_exchange = await channel.declare_exchange(
        Config.TAGS_ADDED_EXCHANGE_NAME,
        ExchangeType.FANOUT,
        durable="true"
    )

    tags_added_message = {
        "movieIds": [movie_id],
        "tags": [tag]
    }

    correlation_id = uuid1()

    message = Message(json.dumps(tags_added_message).encode('utf-8'),
                      delivery_mode=DeliveryMode.PERSISTENT,
                      correlation_id=correlation_id)

    await tags_added_exchange.publish(
        message, Config.ROUTING_KEY
    )

    logging.info("Message sent to add tag:" + tag + " to movieId:" + movie_id +
          " with correlationId:" + str(correlation_id))
