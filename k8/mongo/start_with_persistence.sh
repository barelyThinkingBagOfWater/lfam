./start_common.sh &&
	kubectl create -f persistence/pv.yml &&
	kubectl create -f persistence/statefulSet.yml
