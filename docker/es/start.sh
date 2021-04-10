docker run --rm -d --name elastic -p 9200:9200 -p 9300:9300 --network isolatedNetwork -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch-oss:7.9.2 &
