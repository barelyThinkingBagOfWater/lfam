kubectl create configmap realm-config-map -n internal --from-file=realm-export.json 
kubectl create -f service_lb.yml
kubectl create -f deployment.yml
kubectl create configmap keycloak-config --from-literal=keycloak.host=http://$(kubectl get services -n internal | grep keycloak-service-lb | awk '{print $3}'):8080
kubectl create configmap keycloak-config -n internal --from-literal=keycloak.host=http://$(kubectl get services -n internal | grep keycloak-service-lb | awk '{print $3}'):8080
kubectl create secret generic keycloak-secret --from-literal=keycloak.secret=0cfa4a76-334f-4fb5-a335-3a82fb939944
