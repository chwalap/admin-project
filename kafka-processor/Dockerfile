FROM openjdk:16-alpine3.13

WORKDIR /app
COPY ./avg/kafka-publisher/target/kafka-publisher-1.0-SNAPSHOT-jar-with-dependencies.jar ./kafka.jar
CMD java -cp kafka.jar com.kafka.myapps.MovingAverage

