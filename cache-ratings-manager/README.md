This application manages the cache of the ratings using an event store (involving event sourcing, https://eventstore.org/).
It uses a projection to calculate the average ratings for a movie and the ratings are available through the gateway.

Written in Scala + Play framework for REST handling

You can use the following scripts to:
- deploy.sh will create a new container with the current version of this app and then deploy it in a local Docker network (check docker repo for more details)
- push.sh will create a new container with the current version of this app and then deploy it to a local Docker registry for further use by your local cloud cluster
