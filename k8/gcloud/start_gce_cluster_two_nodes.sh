gcloud container clusters create lfam-cluster --release-channel rapid --num-nodes 1 --machine-type custom-4-4608 --image-type cos_containerd
	gcloud container clusters get-credentials lfam-cluster 
	kubectl label nodes $(kubectl get node | grep Ready | awk '{print $1}') security=internal 
	gcloud container node-pools create dmz-pool --cluster lfam-cluster --num-nodes 1 --machine-type custom-4-8192 --image-type cos_containerd
	kubectl label nodes $(kubectl get node | grep dmz | awk '{print $1}') security=dmz 
	kubectl create namespace internal 
	kubectl create namespace monitoring 
	gcloud compute disks create --size=10GB --zone us-central1-a entities-data-disk
	./createCertificate.sh
