gcloud container clusters create lfam-cluster --release-channel regular --num-nodes 1 --machine-type custom-4-10240 
	gcloud container clusters get-credentials lfam-cluster
	kubectl create namespace internal
	kubectl create namespace monitoring
	gcloud compute disks create --size=10GB --zone us-central1-a entities-data-disk
	./createCertificate.sh
