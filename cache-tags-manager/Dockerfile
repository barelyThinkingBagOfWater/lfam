FROM openjdk:15

#ADD tags/tags_1000.csv /tmp/tags.csv

ADD target/cache-tags-manager.jar /cache-tags-manager.jar
ENTRYPOINT ["java","-jar","-XX:MaxRAMPercentage=75","/cache-tags-manager.jar"]
