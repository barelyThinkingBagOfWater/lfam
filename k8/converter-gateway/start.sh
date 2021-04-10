kubectl create -f config.yml &&
    kubectl create -f deployment.yml &&
    kubectl create -f service.yml &&
    kubectl autoscale deployment converter-gateway-deployment --cpu-percent=70 --min=1 --max=10
