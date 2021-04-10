kubectl create namespace logging &&
    	kubectl create -f es.yml &&
    	kubectl create -f kibana.yml &&
    	kubectl create -f fluentd.yml
