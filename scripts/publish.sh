#!/bin/bash
docker run -it --rm --name mqtt-publisher --network admin-project_default efrecon/mqtt-client \
pub -h mosquitto  -t aaa -m "$(eval ./json.sh)"
