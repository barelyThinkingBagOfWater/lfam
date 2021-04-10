echo "window.REACT_APP_CLUSTER_URL='https://letsfindamovie.com'" > config.js &&  kubectl create configmap webapp-config --from-file=config.js && rm config.js &&
    kubectl create -f deployment_gce.yml &&
    kubectl create -f service.yml 
