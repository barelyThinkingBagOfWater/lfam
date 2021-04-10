kubectl create -f service.yml &&
	helm install elasticsearch-exporter-release stable/elasticsearch-exporter -f exporter_values.yaml
	kubectl create -f persistence/pv0.yml &&
	kubectl create -f persistence/pv1.yml &&
	kubectl create -f persistence/pv2.yml &&
	kubectl create -f persistence/statefulSet.yml
