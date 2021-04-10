#docker run --rm -d --name solr -p 8983:8983 --network isolatedNetwork -v $PWD/config:/opt/solr/server/solr/configsets/tagsconfig:ro solr:8.4 solr-precreate tags /opt/solr/server/solr/configsets/tagsconfig
docker run --rm -d --name solr -p 8983:8983 --network isolatedNetwork -v $PWD/config:/opt/solr/server/solr/configsets/tagsconfig:ro solr:8.4 solr-precreate tags 
