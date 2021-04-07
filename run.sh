#!/bin/sh


#Compile application docker app
mvn clean install -DskipTests

rm docker/fact-manager/*.jar

cp fact-manager-rest/target/factManager.jar docker/fact-manager/

cd docker

docker-compose stop
docker-compose build

docker-compose up -d

