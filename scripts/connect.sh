#!/bin/bash
curl -d @../configs/mqtt.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors
curl -d @../configs/mongo.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors
curl -d @../configs/mqtt2.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors
