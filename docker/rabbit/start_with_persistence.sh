docker run --rm -d --name rabbit --network isolatedNetwork -e RABBITMQ_DEFAULT_USER=rabbit -e RABBITMQ_DEFAULT_PASS=rabbit123 -v /data/rabbit:/var/lib/rabbitmq -h rabbit rabbitmq:3.8.1-management
