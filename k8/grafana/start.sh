kubectl create configmap custom-config-grafana-configmap -n monitoring --from-file=grafana.ini &&
	kubectl create configmap grafana-provisoned-datasources-configmap -n monitoring --from-file=provisioning/datasources/prometheus.yml &&
	kubectl create configmap grafana-provisoned-dashboards-configmap -n monitoring --from-file=provisioning/dashboards/public_dashboard.json --from-file=provisioning/dashboards/all.yml --from-file=provisioning/dashboards/business_metrics.json --from-file=provisioning/dashboards/cache_ratings_manager_metrics_rev1.json --from-file=provisioning/dashboards/elasticsearch_monitoring.json --from-file=provisioning/dashboards/eventstore_monitoring.json --from-file=provisioning/dashboards/jvm-actuator_rev1.json --from-file=provisioning/dashboards/mongodb_monitoring.json --from-file=provisioning/dashboards/redis_monitoring.json &&
	kubectl create -f deployment.yml &&
    	kubectl create -f service.yml

