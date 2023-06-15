FROM ubuntu:latest

RUN apt-get update -y && \
    apt-get install maven netcat -y

COPY kafka-streams/walking-average-humidity /build

WORKDIR /build/
RUN mvn clean package && \
    mkdir -p /app && \
    cp /build/target/walkingaveragehumid-1.0-jar-with-dependencies.jar /app/walkingaveragehumid-1.0.jar

CMD  ["/bin/true"]
