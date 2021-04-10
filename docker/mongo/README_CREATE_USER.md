docker exec bash

mongo admin -u root -p 'toor'

db.createUser({
    user: "mongodb_exporter",
    pwd: "s3cr3tpassw0rd",
    roles: [
        { role: "clusterMonitor", db: "admin" },
        { role: "read", db: "local" }
    ]
});

//big retour

use movies;

db.createUser(
   {
     user: "movies-manager",
     pwd: "movies-manager123",
     roles: [ "readWrite" ]
   }
);


