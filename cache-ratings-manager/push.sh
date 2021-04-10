	sbt dist &&
unzip target/universal/cache-ratings-manager.zip -d target/release &&
docker build . -t localhost:5000/cache-ratings-manager &&
rm -r target &&
docker push localhost:5000/cache-ratings-manager
