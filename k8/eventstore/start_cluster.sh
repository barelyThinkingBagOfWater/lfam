kubectl create -f secret.yml &&
	kubectl create -f service_cluster.yml &&
	kubectl create -f statefulset_cluster.yml &&
	kubectl create -f service_exporter.yml &&
	kubectl create -f deployment_exporter_cluster.yml
