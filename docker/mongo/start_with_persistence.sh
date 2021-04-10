docker run --rm -d --name mongo-service --network isolatedNetwork -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=toor -v /data/mongo:/data/db mongo:4.2
