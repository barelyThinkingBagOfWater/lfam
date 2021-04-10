kubectl create -f cluster_role.yml &&
	kubectl create -f service_account.yml &&
	kubectl create -f service.yml &&
	kubectl create -f statefulSet.yml &&
	echo "Now waiting for mongo to be up to create the users" &&
	until $(kubectl get pods | grep mongo-statefulset-2 | grep -q "2/2")
	do
		sleep 1;
	done
	kubectl exec -it mongo-statefulset-0 -- mongo admin --eval 'db.createUser({user:"mongodb_exporter",pwd:"s3cr3tpassw0rd",roles:[{role:"clusterMonitor",db:"admin"},{role:"read",db:"local"}]});db=db.getSiblingDB("movies");db.createUser({user:"movies-manager",pwd:"movies-manager123",roles:["readWrite"]});' &&
	#You could extract the user and password from the secret using kubectl and base64
	kubectl autoscale statefulset mongo-statefulset --cpu-percent=70 --min=3 --max=10
