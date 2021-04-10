FROM hazelcast/hazelcast:4.1.2


ENV BUCKET4J_VERSION 6.0.2
ENV JCACHE_VERSION 1.1.1

ENV HZ_LIB $HZ_HOME/lib
RUN mkdir -p $HZ_LIB
WORKDIR $HZ_LIB

# Download jcache and bucket4j jars from maven repo.
ADD https://search.maven.org/remotecontent?filepath=com/hazelcast/hazelcast-all/4.1.1/hazelcast-all-4.1.1.jar $HZ_LIB
ADD https://search.maven.org/remotecontent?filepath=javax/cache/cache-api/1.1.1/cache-api-1.1.1.jar $HZ_LIB
ADD https://search.maven.org/remotecontent?filepath=com/github/vladimir-bukhtoyarov/bucket4j-core/6.0.2/bucket4j-core-6.0.2.jar $HZ_LIB
ADD https://search.maven.org/remotecontent?filepath=com/github/vladimir-bukhtoyarov/bucket4j-jcache/6.0.2/bucket4j-jcache-6.0.2.jar $HZ_LIB
ADD https://search.maven.org/remotecontent?filepath=com/github/vladimir-bukhtoyarov/bucket4j-hazelcast/6.0.2/bucket4j-hazelcast-6.0.2.jar $HZ_LIB


#Original server.sh expected all eternal libs to be in a folder set by the CLASSPATH env variable. This is misleading and captured here: https://github.com/hazelcast/hazelcast-docker/issues/45
ENV CLASSPATH $HZ_LIB

WORKDIR $HZ_HOME

# Add your custom hazelcast.xml
ADD hazelcast.xml $HZ_HOME

# Run hazelcast
EXPOSE 5701

CMD ["/opt/hazelcast/start-hazelcast.sh"]
