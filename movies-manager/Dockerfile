FROM openjdk:15

ADD static-resources/movies.csv /tmp/movies.csv
#ADD static-resources/ratings_1M.csv /tmp/ratings.csv
ADD static-resources/ratings_1000.csv /tmp/ratings.csv
ADD static-resources/tags_1000.csv /tmp/tags.csv
#ADD static-resources/tags.csv /tmp/tags.csv

ADD src/main/resources/certs/db_client.jks /certs/
ADD src/main/resources/certs/db_client.ts /certs/
ADD src/main/resources/certs/services_keystore.p12 /certs/

ADD target/movies-manager.jar /movies-manager.jar
ENTRYPOINT ["java","-jar","-XX:MaxRAMPercentage=75","/movies-manager.jar"]
