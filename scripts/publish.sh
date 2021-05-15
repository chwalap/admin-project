#!/bin/bash
docker run -it --rm --name mqtt-publisher efrecon/mqtt-client pub -h mosquitto  -t temperature -m "$(eval ./temp.sh)"
