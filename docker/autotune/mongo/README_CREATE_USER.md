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

use quotes;

db.createUser(
   {
     user: "quotes-importer",
     pwd: "quotes-importer123",
     roles: [ "readWrite" ]
   }
);

db.createUser(
   {
     user: "backtester",
     pwd: "backtester123",
     roles: [ "read" ]
   }
);



use results;

db.createUser(
   {
     user: "backtester",
     pwd: "backtester123",
     roles: [ "readWrite" ]
   }
);

