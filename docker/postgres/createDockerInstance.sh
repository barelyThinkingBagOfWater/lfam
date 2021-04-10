docker run --rm -d --name postgres --network isolatedNetwork -e POSTGRES_USER=root -e POSTGRES_PASSWORD=toor -v /data/postgres:/var/lib/postgresql/data postgres 

#Just changed db-data to data in shared volume
