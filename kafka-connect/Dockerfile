FROM confluentinc/cp-kafka-connect:latest

COPY ./configs/* /kafka/
COPY ./scripts/wait_for_socket.sh /kafka/

CMD /kafka/wait_for_socket.sh kafka 9092 && sleep 10 && /usr/bin/connect-standalone ${CONFIG}