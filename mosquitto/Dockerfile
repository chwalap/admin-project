FROM eclipse-mosquitto:latest

COPY ./configs/mosquitto.conf /mosquitto/config/
COPY ./configs/passwd /mosquitto/config/

CMD /usr/sbin/mosquitto -c /mosquitto/config/mosquitto.conf
