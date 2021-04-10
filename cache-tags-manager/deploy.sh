#!/usr/bin/env bash
mvn clean install && docker build . -t cache-tags-manager && docker run --rm --network isolatedNetwork -e ES_JAVA_OPTS="-Xms512m -XX:MaxRam2560m -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Xmx2g" --name cache-tags-manager cache-tags-manager
