docker build . -t converter-gateway && docker run --rm -e RABBIT_HOST=rabbit --network isolatedNetwork --name converter-gateway converter-gateway
