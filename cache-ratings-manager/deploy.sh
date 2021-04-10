#!/usr/bin/env bash
sbt dist &&
unzip target/universal/cache-ratings-manager.zip -d target/release &&
docker build . -t cache-ratings-manager &&
rm -r target &&
docker run --rm --network isolatedNetwork -e ES_JAVA_OPTS="-Xms512m -XX:MaxRam2560m -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Xmx2g" --name cache-ratings-manager cache-ratings-manager
