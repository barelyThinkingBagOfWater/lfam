kubectl create -f secret.yml &&
    kubectl create -f service.yml  &&
    kubectl create -f config_singleMongoNode.yml &&
    kubectl create -f deployment_gce.yml
