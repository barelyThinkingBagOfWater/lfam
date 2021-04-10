docker stop redis &&
./start.sh &&
docker logs --follow redis
