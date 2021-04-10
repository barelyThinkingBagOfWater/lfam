kubectl create -f deployment_master.yml &&
	kubectl create -f deployment_data.yml &&
    	kubectl create -f service_master.yml &&
    	kubectl create -f service_data.yml &&
	kubectl autoscale deployment elasticsearch-data-deployment --cpu-percent=70 --min=2 --max=10 &&
	helm install elasticsearch-exporter-release stable/elasticsearch-exporter -f ../exporter_values.yaml 
