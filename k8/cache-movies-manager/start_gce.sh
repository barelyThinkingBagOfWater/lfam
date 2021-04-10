kubectl create -f config_singleRedisNode.yml &&
    kubectl create -f deployment_gce.yml &&
    kubectl create -f service.yml 
