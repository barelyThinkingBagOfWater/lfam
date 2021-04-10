kubectl create -f secret.yml &&        
	cd cluster &&
	./start.sh &&
	helm install mongodb-prometheus-exporter-release prometheus-community/prometheus-mongodb-exporter -f mongo_exporter_helm_cluster_values.yaml
