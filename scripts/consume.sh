#!/bin/bash
docker-compose run -d --rm kafka kafka-console-consumer --bootstrap-server kafka:9092 --topic $1 --from-beginning
