#!/bin/bash
curl -X DELETE http://localhost:8083/connectors/mqtt-source
curl -X DELETE http://localhost:8083/connectors/mongodb-sink
