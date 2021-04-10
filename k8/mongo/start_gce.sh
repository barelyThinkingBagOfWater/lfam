kubectl create -f secret.yml &&        
	kubectl create configmap initscript-configmap -n internal --from-file=init.js &&
	kubectl create -f service.yml &&
	helm install mongodb-prometheus-exporter-release prometheus-community/prometheus-mongodb-exporter -f mongo_exporter_helm_values_gce.yaml --namespace internal &&
	kubectl create -f persistence/gce_pv.yml &&
	kubectl create -f persistence/statefulSet.yml
