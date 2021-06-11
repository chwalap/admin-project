FROM ubuntu:latest

RUN apt-get update -y && apt-get upgrade -y && apt-get install openjdk-16-jdk maven curl netcat -y && apt-get update -y && apt-get upgrade -y && rm -rf ~/.m2

WORKDIR /build
COPY ./* /build/
RUN mvn -U -f ./walking-average/pom.xml package && cp /build/walking-average/target/walkingaverage-1.0.jar /app

CMD  [ "java", "-cp",  "/app/walkingaverage-1.0.jar", "com.walkingaverage.WalkingAverage" ]
