kubectl create -f config.yml &&
	kubectl create -f statefulset.yml &&
	kubectl create -f service.yml &&
	until $(kubectl get pods | grep redis-cluster-5 | grep -q "2/2")
	do
		sleep 1;
	done
	echo "Now creating the Redis cluster" &&
	echo 'yes' | kubectl exec -it redis-cluster-0 -- redis-cli --cluster create --cluster-replicas 1 $(kubectl get pods -l app=redis-cluster -o json | grep \"podIP\" | cut -c27- | sed 's/",/:6379/g' | tr '\n' ' ') &&
	helm install redis-prometheus-exporter-release prometheus-community/prometheus-redis-exporter -f cluster_exporter_values.yaml
