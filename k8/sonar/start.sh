kubectl create -f pv.yml &&
kubectl create -f pvc.yml &&
kubectl create -f deployment.yml &&
kubectl create -f deployment_postgres.yml &&
kubectl create -f service.yml &&
kubectl create -f service_postgres.yml
