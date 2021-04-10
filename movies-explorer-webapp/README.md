Webapp to search movies by tag, consult them and add ratings or tags to them.
Written using React + Redux + Rxjs


You can use the following scripts to:
- start.sh will run the webapp on localhost:8080 with hot reload during development, basically 'npm start'
- deploy.sh will create a new container with the current version of this webapp and then deploy it in a local Docker network (check docker repo for more details)
- push.sh will create a new container with the current version of this webapp and then deploy it to a local Docker registry for further use by your local cloud cluster
