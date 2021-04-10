kubectl create configmap redis-conf-configmap --from-file=redis.conf &&
kubectl create -f deployment.yml &&
	kubectl create -f service.yml &&
	helm install redis-prometheus-exporter-release prometheus-community/prometheus-redis-exporter -f values.yaml
