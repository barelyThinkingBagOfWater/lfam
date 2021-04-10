helm install cache-tags-release . -f values.yaml &&
	kubectl autoscale deployment elasticsearch-data-deployment --cpu-percent=70 --min=2 --max=10 &&
	kubectl autoscale deployment cache-tags-manager-deployment --cpu-percent=70 --min=1 --max=10 &&
	helm install elasticsearch-exporter-release stable/elasticsearch-exporter -f ../exporter_values.yaml
