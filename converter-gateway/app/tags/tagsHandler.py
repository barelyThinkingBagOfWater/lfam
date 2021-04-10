from aiohttp import web
from prometheus_client import Counter

from tags import tagsMessagesSender


class TagsHandler:
    loop = object()
    tag_added_counter = Counter('tags_added', 'Number of tags added', ['application']) \
        .labels('converter-gateway')

    def __init__(self, loop):
        self.loop = loop

    async def add_tag_to_movie(self, request):
        movie_id = request.match_info.get("movieId")
        tag = request.match_info.get("tag")

        await tagsMessagesSender.send_tags_added_message(self.loop, movie_id, tag)
        self.tag_added_counter.inc()

        return web.Response()

# Metrics done? :)
