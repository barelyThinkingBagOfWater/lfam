Kubernetes scripts used to instantiate the k8 cluster and infrastructure (including the monitoring and log aggregation parts) + every components of the system.
Every module has its separated file for maintenance/testing purposes.
Everything is stateless, no pv or pvc required.

To start:
 - run the start_all.sh script in the root folder to start a local kubernetes cluster and deploy there every component of the system
 - each module has its dedicated start scripts with a single instance mode and a cluster mode

You can access the webapp at the address CLUSTER_IP_OR_URL/webapp through the gateway.


Prerequisites:
- Install kubeadm : https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/
- Don't forget to disable the swap before starting the cluster : sudo swapoff -a (sudo swapon -a to turn it back on)
