#kubectl rollout restart deployment mongo-deployment && sleep 3 &&
kubectl rollout restart deployment movies-manager-deployment && sleep 5 &&
kubectl rollout restart deployment elasticsearch-deployment &&
kubectl rollout restart deployment cache-tags-manager-deployment
