FROM confluentinc/cp-kafka-connect:latest
WORKDIR /etc/kafka-connect/jars/
RUN tar -cvf all.tar /usr/share/java/* && tar -xvf all.tar --strip=4
CMD /etc/confluent/docker/run
