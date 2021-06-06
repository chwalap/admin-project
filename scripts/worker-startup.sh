#!/bin/sh

/etc/scripts/wait-for-socket.sh kafka 9092 && /usr/bin/connect-distributed /etc/configs/worker.properties
