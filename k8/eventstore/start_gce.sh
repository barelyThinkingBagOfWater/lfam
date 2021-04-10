kubectl create -f secret.yml &&
kubectl create -f deployment_gce.yml &&
	kubectl create -f service_single.yml &&
	kubectl create -f deployment_exporter_single_gce.yml &&
	kubectl create -f service_exporter.yml
