#!/bin/bash
curl -d @../configs/connect-mqtt-source.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors
curl -d @../configs/connect-mongodb-sink.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors
