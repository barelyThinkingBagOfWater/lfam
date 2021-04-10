kubectl create -f config_single.yml &&
    kubectl create -f deployment.yml &&
    kubectl create -f service.yml &&
    kubectl autoscale deployment cache-ratings-manager-deployment --cpu-percent=70 --min=1 --max=10
