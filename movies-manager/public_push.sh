mvn clean install &&
#docker build . -t xbarrelet/movies-manager:1.0.0_light &&
#docker push xbarrelet/movies-manager:1.0.0_light
#docker build . -t xbarrelet/movies-manager:1.0.0 &&
#docker push xbarrelet/movies-manager:1.0.0
docker build . -t xbarrelet/movies-manager &&
docker push xbarrelet/movies-manager
