FROM ubuntu:latest

RUN apt-get update -y && apt-get install mosquitto-clients netcat -y
CMD [ "/usr/bin/mosquitto_pub" ]
