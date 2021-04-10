db = db.getSiblingDB('admin');
db.createUser({
    user: "mongodb_exporter",
    pwd: "s3cr3tpassw0rd",
    roles: [
        { role: "clusterMonitor", db: "admin" },
        { role: "read", db: "local" }
    ]
});

db = db.getSiblingDB('movies');

db.createUser(
   {
     user: "movies-manager",
     pwd: "movies-manager123",
     roles: [ {role: "readWrite", db: "movies"} ]
   }
);

