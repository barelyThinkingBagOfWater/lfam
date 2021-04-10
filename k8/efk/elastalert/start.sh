kubectl create configmap elastalert-rules -n logging --from-file=frequency_rule.yml &&
kubectl create -f config.yml &&
kubectl create -f deployment.yml
