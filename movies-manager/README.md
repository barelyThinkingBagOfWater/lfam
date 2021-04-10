This application manages the source of truth of the system that contains every movies, tags and ratings that then will be fed to the caches. 
It reads the entities from csv files located in the static-resources folder. Different files can be incorporated in the Docker container using the Dockerfile, useful to not import millions of entities during tests.
Written in Java using Spring's webflux and Springboot.

You can use the following scripts to:
- deploy.sh will create a new container with the current version of this app and then deploy it in a local Docker network (check docker repo for more details)
- push.sh will create a new container with the current version of this app and then push it to a local Docker registry for further use by your local cloud cluster
