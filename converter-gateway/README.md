This application converts REST calls to add tags and ratings to movies to events and propagates them into the system (caches + source of truth)
Written in Python using asynchronous rest and messaging handling (aiohttp + aio_pika).

You can use the following scripts to:
- deploy.sh will create a new container with the current version of this app and then deploy it in a local Docker network (check docker repo for more details)
- push.sh will create a new container with the current version of this app and then deploy it to a local Docker registry for further use by your local cloud cluster