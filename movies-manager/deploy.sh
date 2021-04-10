#!/usr/bin/env bash
mvn clean install && docker build . -t movies-manager && docker run --rm --network isolatedNetwork --name movies-manager movies-manager
