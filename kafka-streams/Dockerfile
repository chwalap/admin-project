FROM ubuntu:latest

COPY kafka-streams/walking-average /build
WORKDIR /build/
RUN apt-get update -y && \
    apt-get install maven -y && \
    mvn clean package && \
    mkdir -p /app && \
    cp /build/target/walkingaverage-1.0-jar-with-dependencies.jar /app/walkingaverage-1.0.jar

CMD  [ "java", "-cp",  "/app/walkingaverage-1.0.jar", "com.walkingaverage.WalkingAverage" ]
