mvn clean install &&
docker build . -t xbarrelet/cache-tags-manager &&
docker push xbarrelet/cache-tags-manager
