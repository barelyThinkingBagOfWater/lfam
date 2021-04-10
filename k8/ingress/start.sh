helm install ingress-release -f values.yaml ingress-nginx/ingress-nginx &&
       until $(kubectl get pods | grep ingress-release-ingress-nginx-controller | grep -q "1/1")
       do
               sleep 2;
       done &&
       kubectl create -f ingress.yml
