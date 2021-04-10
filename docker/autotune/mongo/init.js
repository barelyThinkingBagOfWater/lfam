db.createUser({
    user: "mongodb_exporter",
    pwd: "s3cr3tpassw0rd",
    roles: [
        { role: "clusterMonitor", db: "admin" },
        { role: "read", db: "local" }
    ]
});

db = db.getSiblingDB('quotes');

db.createUser(
   {
     user: "backtester",
     pwd: "backtester123",
     roles: [ {role: "read", db: "quotes"} ]
   });
   
db.createUser(
   {
     user: "quotes-importer",
     pwd: "quotes-importer123",
     roles: [ {role: "readWrite", db: "quotes"} ]
   });


db = db.getSiblingDB('results');

db.createUser(
   {
     user: "backtester",
     pwd: "backtester123",
     roles: [ {role: "readWrite", db: "results"} ]
   });
