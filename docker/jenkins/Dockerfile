FROM jenkins/jenkins:jdk11

USER root
COPY jdk-13.0.2 /opt/jdk-13.0.2
COPY apache-maven-3.6.3 /opt/apache-maven-3.6.3

RUN ln -s /opt/apache-maven-3.6.3/bin/mvn /usr/bin/mvn #I could also simply set MVN_HOME in the pipeline

#Doesn't work but this is what's missing and causes the https call by default
#RUN echo "DOCKER_OPTS=--insecure-registry=10.244.0.1:5000" >> /etc/default/docker

USER jenkins
