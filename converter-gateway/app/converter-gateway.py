from aiohttp import web
import asyncio
from prometheus_client import start_http_server as start_prometheus_endpoint
import logging

from ratings.ratingsHandler import RatingsHandler
from tags.tagsHandler import TagsHandler

url_prefix = "/api/"

async def init(loop):
    app = web.Application()
    tags_handler = TagsHandler(loop)
    ratings_handler = RatingsHandler(loop)
    logging.basicConfig(level=logging.INFO, format='%(asctime)s %(message)s', datefmt='%d/%m/%Y %I:%M:%S %p')

    app.add_routes([
        web.put(url_prefix + "movie/{movieId}/tag/{tag}", tags_handler.add_tag_to_movie),
        web.put(url_prefix + "movie/{movieId}/rating/{rating}", ratings_handler.add_rating_to_movie)
    ])
    return app


if __name__ == '__main__':
    event_loop = asyncio.get_event_loop()
    webapp = event_loop.run_until_complete(init(event_loop))
    start_prometheus_endpoint(8000)

    logging.info("Converter-gateway now started.")
    web.run_app(webapp, port=80)
