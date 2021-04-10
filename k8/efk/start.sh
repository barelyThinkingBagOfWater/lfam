#!/bin/bash

GREEN='\033[1;32m'
RESET_COLOR='\033[0m'

print () {
  echo -e "${GREEN}$1${RESET_COLOR}"
}


kubectl create namespace logging &&
    	kubectl create -f es.yml &&
    	kubectl create -f fluentd.yml &&
    	kubectl create -f kibana.yml &&
	echo &&
	print "Now waiting for Kibana + dedicated ElasticSearch DB to be up before creating the default logstash index patterns" &&
	echo &&
	until $(kubectl logs $(kubectl get pods -n logging | grep kibana | awk '{print $1}') -n logging | grep -q "Server running at http://0:5601")
	do
		sleep 1;
	done
	curl -X POST "$(kubectl get services -n logging | grep kibana | awk '{print $3}'):5601/api/saved_objects/_import" -H "kbn-xsrf: true" --form file=@logstash_index_pattern.ndjson
