mvn clean install && 
	docker build . -t localhost:5000/cache-tags-manager &&
	docker push localhost:5000/cache-tags-manager