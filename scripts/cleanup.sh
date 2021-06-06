#!/bin/sh

curl -X DELETE http://kafka-connect:8083/connectors/mosquitto-mqtt
curl -X DELETE http://kafka-connect:8083/connectors/mongodb-sink
