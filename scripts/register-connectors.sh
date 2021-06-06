#!/bin/sh

./wait-for-socket.sh kafka-mqtt 8083
./wait-for-socket.sh kafka-mongodb 8083

sleep 1
curl -s -d @../configs/mqtt.json -H "Content-Type: application/json" -X POST http://kafka-mqtt:8083/connectors
curl -s -d @../configs/mongodb.json -H "Content-Type: application/json" -X POST http://kafka-mongodb:8083/connectors
