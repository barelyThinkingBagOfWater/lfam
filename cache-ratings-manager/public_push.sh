sbt dist &&
unzip target/universal/cache-ratings-manager.zip -d target/release &&
docker build . -t xbarrelet/cache-ratings-manager &&
rm -r target &&
docker push xbarrelet/cache-ratings-manager
