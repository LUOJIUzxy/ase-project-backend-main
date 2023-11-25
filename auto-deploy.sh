#!/bin/sh

# build jar files
mvn clean install

# build/update docker images
docker-compose build

# deploy images
docker-compose up