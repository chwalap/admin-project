FROM ubuntu:latest

RUN apt-get update -y && \
    apt-get install maven netcat -y

COPY kafka-streams/walking-average-temperature /build
WORKDIR /build/
RUN mvn clean package && \
    mkdir -p /app && \
    cp /build/target/walkingaveragetemp-1.0-jar-with-dependencies.jar /app/walkingaveragetemp-1.0.jar

CMD ["/bin/true"]
