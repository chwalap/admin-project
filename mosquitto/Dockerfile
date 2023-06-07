FROM eclipse-mosquitto:latest

RUN apk update && apk upgrade && apk add bash netcat-openbsd curl
COPY ./configs/mosquitto.conf /mosquitto/config/
CMD /usr/sbin/mosquitto -c /mosquitto/config/mosquitto.conf
