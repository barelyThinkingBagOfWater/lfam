#!/usr/bin/env bash
npm run-script build && 
	docker build . -t movies-explorer-webapp && 
	docker run --rm --name webapp --network isolatedNetwork movies-explorer-webapp
