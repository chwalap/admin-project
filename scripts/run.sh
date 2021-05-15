#!/bin/bash
docker-compose up --detach
./connect.sh
./consume.sh temperature

while true ; do ./publish.sh temperature & sleep 5; done
