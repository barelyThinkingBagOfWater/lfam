sudo kubeadm init --pod-network-cidr=10.244.0.0/16 &&
	sudo cp -rf /etc/kubernetes/admin.conf $HOME/.kube/config &&
	kubectl get pods &&
	kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml &&
	kubectl taint nodes --all node-role.kubernetes.io/master- &&
	kubectl config set-context localCluster --namespace=localNamespace &&
	kubectl config use-context localCluster &&
	sudo cp -rf /etc/kubernetes/admin.conf $HOME/.kube/config &&
	kubectl create -f autoscaling/metrics-server-components.yaml &&
	kubectl create namespace internal &&
	kubectl create namespace monitoring
