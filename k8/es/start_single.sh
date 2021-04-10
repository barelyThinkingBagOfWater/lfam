kubectl create -f deployment.yml &&
    	kubectl create -f service.yml &&
	helm install elasticsearch-exporter-release stable/elasticsearch-exporter -f exporter_values.yaml
