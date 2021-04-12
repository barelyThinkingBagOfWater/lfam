mvn clean install -Dmaven.test.skip=true &&
	docker build . -t localhost:5000/cache-movies-manager &&
	docker push localhost:5000/cache-movies-manager