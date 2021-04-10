npm run-script build &&
       	docker build . -t localhost:5000/movies-explorer-webapp && 
	docker push localhost:5000/movies-explorer-webapp
