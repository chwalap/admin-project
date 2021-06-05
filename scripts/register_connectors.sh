#!/bin/sh

current_path=$(dir .)
config_path=${current_path}/../configs

${current_path}/wait_for_socker.sh kafka-connect 8083

sleep 1
${current_path}/cleanup.sh

sleep 1
curl -d @${current_path}/mqtt.json -H "Content-Type: application/json" -X POST http://kafka-connect:8083/connectors
curl -d @${current_path}/mongodb.json -H "Content-Type: application/json" -X POST http://kafka-connect:8083/connectors
