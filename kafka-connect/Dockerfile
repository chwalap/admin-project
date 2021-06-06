FROM confluentinc/cp-kafka-connect:5.1.0

COPY ./configs/* /kafka/
COPY ./scripts/wait-for-socket.sh /kafka/

CMD /kafka/wait-for-socket.sh kafka 9092 && sleep 10 && /usr/bin/connect-standalone ${CONFIG}