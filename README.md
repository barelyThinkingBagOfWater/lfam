The components of the website letsfindamovie.com that uses a CQRS architecture and everything is reactive and hosted in a Kubernetes cluster. Made of:
  - movies-manager : Source of truth of the system that loads from csv files the movies, tags and ratings and serves them to all the caches
  - converter-gateway: Converts rest calls to events propagated within the system to add tags and ratings to movies in all components, written in async Python with Aiohttp
  - cache-tags-manager : reactive cache of tags using ElasticSearch as a datastore
  - cache-movies-manager: reactive cache of movies using Redis as datastore and Micronaut as a Framework.
  - cache-movies-manager-haskell: same cache but coded in Haskell, still needs to implement the update of movies in Redis
  - cache-ratings-manager: reactive cache of ratings coded in Scala using Akka and Play as framework, with Eventstore as a datastore (event sourcing)
  - movies-explorer-webapp: React/Redux/RxJs webapp to consult and edit movies/tags/ratings + to consult Grafana dashboards about the state of every components (technical + business) + a diagram of the architecture with the link to this repo
  - k8: Scripts and configuration to start the system in Kubernete clusters locally and on Google Cloud
  - docker: Scripts to load the components in Docker as a dev/test environment
