FROM ubuntu:latest
RUN apt-get update -y && apt-get upgrade -y && apt-get install openjdk-16-jre curl netcat -y

#COPY ./scripts/wait-for-socket.sh /kafka/
WORKDIR /app
COPY ./moving-average/kafka-publisher/target/kafka-publisher-1.0-SNAPSHOT-jar-with-dependencies.jar ./kafka.jar
CMD  sleep 60 && java -cp kafka.jar com.kafka.myapps.MovingAverage
