from aiohttp import web
from prometheus_client import Counter

from ratings import ratingsMessagesSender


class RatingsHandler:
    loop = object()
    rating_added_counter = Counter('ratings_added', 'Number of ratings added', ['application'])\
        .labels('converter-gateway')

    def __init__(self, loop):
        self.loop = loop

    async def add_rating_to_movie(self, request):
        movie_id = request.match_info.get("movieId")
        rating = request.match_info.get("rating")

        await ratingsMessagesSender.send_rating_added_message(self.loop, movie_id, rating)
        self.rating_added_counter.inc()

        return web.Response()
