kubectl create -f config_singleRedisNode.yml &&
    kubectl create -f deployment.yml &&
    kubectl create -f service.yml &&
    kubectl autoscale deployment cache-movies-manager-deployment --cpu-percent=70 --min=1 --max=10
