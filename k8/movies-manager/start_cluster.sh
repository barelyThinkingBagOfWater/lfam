kubectl create -f secret.yml &&
    kubectl create -f service.yml  &&
    kubectl create -f config_cluster.yml &&
    kubectl create -f deployment.yml  &&
    kubectl autoscale deployment movies-manager-deployment --cpu-percent=70 --min=1 --max=10
