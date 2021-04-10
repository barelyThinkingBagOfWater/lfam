kubectl create -f clusterRole.yml &&
    kubectl create -f service.yml &&
    kubectl create configmap prometheus-config -n monitoring --from-file=prometheus.yml --from-file=alerting_rules.yml  &&
    kubectl create -f deployment.yml 
    #cd alertmanager &&
    #./start.sh
