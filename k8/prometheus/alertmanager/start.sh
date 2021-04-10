kubectl create configmap alertmanager-config -n monitoring --from-file=alertmanager_config.yml &&
    kubectl create configmap alertmanager-templates -n monitoring --from-file=alertmanager_templates.yml &&
    kubectl create -f alertmanager_deployment.yml &&
    kubectl create -f alertmanager_service.yml
