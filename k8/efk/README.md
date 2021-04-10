If you need to create the index pattern manually you can use the following curl: 
curl -X POST "$(kubectl get services -n logging | grep kibana | awk '{print $3}'):5601/api/saved_objects/_import" -H "kbn-xsrf: true" --form file=@logstash_index_pattern.ndjson
