docker run --rm -d --name redis1 --network isolatedNetwork -p 6379:6379 redis:6
docker run --rm -d --name redis2 --network isolatedNetwork -p 6378:6379 redis:6
docker run --rm -d --name redis3 --network isolatedNetwork -p 6377:6379 redis:6
