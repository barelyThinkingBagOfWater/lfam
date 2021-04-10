kubectl create -f secret.yml &&        
	kubectl create configmap initscript-configmap -n internal --from-file=init.js &&
	#kubectl create -f ssl_config.yml &&
	#kubectl create secret generic ssl-mongo-secrets --from-file=ssl/root-ca.pem --from-file=ssl/mongodb.pem &&
	kubectl create -f service.yml &&
	helm install mongodb-prometheus-exporter-release prometheus-community/prometheus-mongodb-exporter -f mongo_exporter_helm_values.yaml --namespace internal
