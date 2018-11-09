#!/bin/bash

jarPid=`jps -l | grep houzzscrapper.jar | awk '{print $1}'`;

if [ -z $jarPid ]; then

    cd ~/scrapping/houzzScrapper/
    nohup java -jar houzzscrapper.jar &

fi
