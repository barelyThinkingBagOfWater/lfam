This application manages the cache of the tags using Elasticsearch as a store in a reactive way.
Written in Java using Spring's Webflux and Springboot.

You can use the following scripts to:
- deploy.sh will create a new container with the current version of this app and then deploy it in a local Docker network (check docker repo for more details)
- push.sh will create a new container with the current version of this app and then deploy it to a local Docker registry for further use by your local cloud cluster
