docker run --rm -d --name rabbit --network isolatedNetwork -e RABBITMQ_DEFAULT_USER=rabbit -e RABBITMQ_DEFAULT_PASS=rabbit123 -h rabbit rabbitmq:3.8-management
