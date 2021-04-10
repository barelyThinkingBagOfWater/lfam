kubectl apply -f cert_namespace.yml
kubectl label namespace cert-manager certmanager.k8s.io/disable-validation="true"
kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v1.1.0/cert-manager.crds.yaml
helm install --namespace cert-manager cert-manager-release jetstack/cert-manager
until $(kubectl get pods -n cert-manager | grep cert-manager-release-webhook | grep -q "1/1")
	do
		sleep 1;
       	done
kubectl create -f issuer.yml
kubectl create -f certificate.yml
