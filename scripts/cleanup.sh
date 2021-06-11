#!/bin/sh

curl -X DELETE http://kafka-mqtt:8083/connectors/mqtt
curl -X DELETE http://kafka-mongodb:8083/connectors/mongodb
