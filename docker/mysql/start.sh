docker run --rm -d --name mysql --network isolatedNetwork -p 3306:3306 -e MYSQL_ROOT_PASSWORD=toor -e MYSQL_USER=cache-movies-manager -e MYSQL_PASSWORD=cache-movies-manager123 -h mysql mysql:8
