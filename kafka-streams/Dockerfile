FROM ubuntu:latest

RUN apt-get update -y && apt-get install openjdk-16-jdk maven curl netcat -y

# build fat jar
WORKDIR /build
COPY ./* /build/
RUN mvn -f ./walking-average/pom.xml package

WORKDIR /app
COPY /build/walking-average/target/walkingaverage-1.0.jar ./

CMD  [ "java", "-cp",  "./walkingaverage-1.0.jar", "com.walkingaverage.WalkingAverage" ]
