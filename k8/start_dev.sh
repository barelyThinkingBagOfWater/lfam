#!/bin/bash

GREEN='\033[1;32m'
RESET_COLOR='\033[0m' 

startComponentWithCurrentMode () {
  cd $1 && ./start_$CURRENT_MODE.sh && cd ..
}

print () {
  echo -e "${GREEN}$1${RESET_COLOR}"
}

echo
echo
print "A local kubernetes cluster will be created using kubeadm with Flannel taking care of the networking, or a cluster will be instantiated on Google Kubernetes Engine."
print "Please make sure each component of the system has been pushed to a local docker registry using the push.sh scripts."
echo


CURRENT_MODE="single"

echo 

echo 
	./start_k8_cluster.sh 
	echo 
	print "Now waiting for the k8 cluster to be up before going forward." 
	echo 

	until $(kubectl get pods -n kube-system | grep kube-scheduler | grep -q "1/1")
       		do
               		sleep 1;
       		done 

echo 
echo 
#./start_centralized_logging.sh &&

echo 
echo 
#./start_monitoring.sh

startComponentWithCurrentMode "rabbit" 
startComponentWithCurrentMode "mongo" 
startComponentWithCurrentMode "eventstore"
startComponentWithCurrentMode "keycloak"

