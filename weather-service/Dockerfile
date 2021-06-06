FROM eclipse-mosquitto:latest

COPY ./scripts/wait-for-socket.sh ./scripts/temp.sh /sensor/

WORKDIR /sensor
CMD ./wait-for-socket.sh mosquitto 1883; ./temp.sh | /usr/bin/mosquitto_pub -h mosquitto -p 1883 -t temperature -c -i 123 -l
