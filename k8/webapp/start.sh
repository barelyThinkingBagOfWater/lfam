echo "window.REACT_APP_CLUSTER_URL='https://$(kubectl get service ingress-release-ingress-nginx-controller | grep ingress | awk '{print $3}')'" > config.js &&  kubectl create configmap webapp-config --from-file=config.js && rm config.js &&
    kubectl create -f deployment.yml &&
    kubectl create -f service.yml &&
    kubectl autoscale deployment webapp-deployment --cpu-percent=70 --min=1 --max=10

