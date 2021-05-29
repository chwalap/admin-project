FROM confluentinc/cp-kafka-connect:5.1.0

COPY ./configs/worker.properties ./configs/mqtt.properties ./configs/mongodb.properties /kafka/
COPY ./scripts/wait_for_socket.sh /kafka/

CMD /kafka/wait_for_socket.sh kafka 9092 && sleep 10 && /usr/bin/connect-standalone /kafka/worker.properties ${CONFIG}