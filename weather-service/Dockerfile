FROM python:3.9-slim

RUN apt-get update && apt-get install netcat-openbsd curl bash -y
RUN pip install --no-cache-dir paho-mqtt docker-compose
COPY ./weather-service/weather_service.py .

CMD ["/bin/true"]
