kubectl create configmap custom-config-grafana-configmap -n monitoring --from-file=grafana.ini &&
kubectl create -f deployment.yml &&
    	kubectl create -f service.yml 
