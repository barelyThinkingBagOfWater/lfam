#!/bin/bash

GREEN='\033[1;32m'
RESET_COLOR='\033[0m' 

startComponentWithCurrentMode () {
  cd $1 && ./start_$CURRENT_MODE.sh && cd ..
}

print () {
  echo -e "${GREEN}$1${RESET_COLOR}"
}

echo
echo
print "Google cloud here I come. A cluster should be ready for me." 
echo
echo

CURRENT_MODE="gce"
echo 
print "The selected mode is $CURRENT_MODE, everything will now be started."
echo 
./start_monitoring.sh &&

startComponentWithCurrentMode "rabbit" 
startComponentWithCurrentMode "mongo" 
startComponentWithCurrentMode "movies-manager" 
startComponentWithCurrentMode "redis" 
startComponentWithCurrentMode "es" 
startComponentWithCurrentMode "eventstore" 
startComponentWithCurrentMode "cache-movies-manager" 
startComponentWithCurrentMode "cache-ratings-manager" 
startComponentWithCurrentMode "cache-tags-manager" 
startComponentWithCurrentMode "converter-gateway" 
startComponentWithCurrentMode "webapp" 
#startComponentWithCurrentMode "ingress" 
