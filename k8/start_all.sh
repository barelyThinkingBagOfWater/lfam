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
print "How would you like to instantiate the components of the system?"
print "1. As single instances"
print "2. In cluster mode"
print "3. On Google Kubernetes Engine"
echo 
print "Please enter your choice: 1, 2 or 3 and press [ENTER]:"

read selected_mode

if [[ $selected_mode == 1 ]]; then
	CURRENT_MODE="single"
elif [[ $selected_mode == 2 ]]; then
	CURRENT_MODE="cluster"
elif [[ $selected_mode == 3 ]]; then
	CURRENT_MODE="gce"
else
	print "Please only pick 1, 2 or 3 (1=single instances, 2=cluster mode, 3=GKE). Aborting script."
	exit 1
fi

echo 
print "The selected mode is $CURRENT_MODE, everything will now be started."
print "A lot of docker images will be downloaded the first time, this will take a few minutes."

echo 
if [[ $selected_mode == 3 ]]; then
	cd gcloud && ./start_gce_cluster_two_nodes.sh && cd ..
else
	./start_k8_cluster.sh 
	echo 
	print "Now waiting for the k8 cluster to be up before going forward." 
	echo 

	until $(kubectl get pods -n kube-system | grep kube-scheduler | grep -q "1/1")
       		do
               		sleep 1;
       		done 

fi
echo 
print "Now starting the centralized logging system in namespace logging." 
echo 
#./start_centralized_logging.sh &&

echo 
print "Now starting the monitoring system in namespace monitoring." 
echo 
./start_monitoring.sh

startComponentWithCurrentMode "keycloak" #not yet with google cloud, to implement after current job
sleep 20 &&
startComponentWithCurrentMode "rabbit" 
startComponentWithCurrentMode "mongo" 
startComponentWithCurrentMode "movies-manager"
startComponentWithCurrentMode "redis"
startComponentWithCurrentMode "es"
startComponentWithCurrentMode "eventstore"
startComponentWithCurrentMode "cache-movies-manager" 
startComponentWithCurrentMode "cache-ratings-manager" 
startComponentWithCurrentMode "cache-tags-manager" 
startComponentWithCurrentMode "converter-gateway" 

startComponentWithCurrentMode "ingress" 
startComponentWithCurrentMode "webapp" 


echo 
print "The last components are being loaded. Here is how you can use the system if it's not running on gce:" 
echo 
print "To check the state of each pod of the system in real time:" 
print "watch -n 1 kubectl get pods --all-namespaces" 
echo 
print "To consult Prometheus metrics go to http://localhost:30001" 
echo 
print "To consult Grafana go to http://localhost:30002 then log in as admin:admin, skip the new password creation and go to dashboards -> manage or simply create your users" 
echo 
print "To check the logs on Kibana go to http://localhost:30003/app/discover" 
echo 
print "To access the webapp go to https://$(kubectl cluster-info | grep control | awk -F'/' '{print $3}' | awk -F: '{print $1}')/"
echo
