Done following https://medium.com/@vladroff/mongodb-ssl-auth-on-kubernetes-ee14bf1a744f

for the java part check the README.md in movies-manager/src/main/resources/certs.

And of course, hello Github!

For the cluster you need to generate different certificates for each node, otherwise it won't work. Or use an operator to set up the cluster.
