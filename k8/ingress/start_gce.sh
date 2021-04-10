helm install ingress-release ingress-nginx/ingress-nginx &&
	until $(kubectl get pods | grep ingress-release-ingress-nginx-controller | grep -q "1/1")
        do
               sleep 2;
        done &&
        kubectl create -f nginx_gce_ingress.yml
