# Using Docker for the management of microservices: Kafka, MongoDB, MQTT broker

## Cel projektu
Celem projektu było stworzenie systemu opartego na mikroserwisach zarządzanych Dockerem. Mieliśmy przy tym użyć poniższych technologii:
* Kafka
* MongoDB
* MQTT broker (Mosquitto :mosquito:	)

---


## Mikroserwisy
Nasz projekt składa się z kilku mikroserwisów:
1. Mosquitto broker - odpowiedzialny za zarządzanie kolejkami wiadomości wymienianych w systemie.
2. Temperature service - skrypt mockujący odczyty z sensora temperatury.
3. Humidity service - skrypt mockujący odczyty z sensora wilgotności.
4. Zookeeper - serwis konfuguracyjny i synchronizujący instancje Kafki.
5. Kafka - centrum całego systemu umożliwiające komunikację i wymianę danych.
6. Kafka Connector - serwis streamujący dane pomiędzy serwisami tj. Mosquitto, Kafka Streams, Kafka MongoDD.
7. Kafka Streams Walking Average Temperature - stateful serwis wyliczający średnią krokową temperatury.
8. Kafka Streams Walking Average Humidity - stateful serwis wyliczający średnią krokową wilgotności.
9. MongoDB - instancja bazy danych temperatury, wilgotności i odpowiadającym im średnim krokowym.

---


## Architektura
### System
Centrum systemu jest serwer Kafki obsługujący jedną instancje Kafka Connect z trzema konektorami oraz dwie customowe instancję Kafka Streams.
Niezależnie od Kafki działają jeszcze: baza noSQL MongoDB oraz broker MQTT Mosquitto.
Do generowania danych został przygotowany prosty skrypt w Pythonie [weather_service.py](/weather-service/weather_service.py), który generuje dane o temperaturze i wilgotności dla serwisów Kafka Streams.
Każdy z mikroserwisów działa w osobnym kontenerze Dockera i wystawia na zewnątrz niezbędne do kooperacji porty.

### Docker
Do administracji systemu został stworzony plik [docker-compose.yml](/docker-compose.yml), który oprócz obsługi wspomnianych mikroserwisów zarządza również kolejnością ich uruchamiania. Każdy z mikroserwisów czeka na nizbędne dla jego działania elementy systemu. Hierarcha zależności wygląda następująco:

- MongoDB
- Zookeeper
  - Kafka
    - Kafka Connect
      - Mosquitto
        - Temperature service
        - Humidity service
        - Kafka Streams Walking Average Temperature
        - Kafka Streams Walking Average Humidity

Serwisy na każdym poziomie listy startują równolegle. Zapewnia nam to odpowiednią koordynację pomiędzy kontenerami od samego początku, skutkuje czystszymi logami oraz zapobiega zapchaniu się kolejek tematów.

### Tematy
W systemie obsługujemy następujące tematy MQTT:
- Mosquitto:
  - temperature
  - humidity
- Kafka:
  - temperature
  - humidity
  - walking-average-temp
  - walking-average-humid

Tematy są tworzone przed wystartowaniem Mosquitto i tylko jeśli jeszcze nie istnieją. Daje nam to pewność, ze Kafka już działa i poprawnie obsłuży nowe tematy.

### Data flow
Przepływ danych w projekcie zaczyna się w serwisach odpowiedzialnych za generowanie danych na temat temperatury i wilgotności. Są one producentami danych Mosquitto i publikują je w tematach *temperature* i *humidity*. Wynikiem tych generatorów są proste dokumenty JSON z pojedynczym parametrem (*temperature* lub *humidity*) i odpowiadającą mu wartością. Na przykład:
```JSON
{
  "temperature": 42
}
```
```JSON
{
  "humidity": 69
}
```
Następnie konektory Kafka konsumują wspomniane dane z tematów Mosquitto i przekazują je do identycznych tematów w Kafce.

Kolejnym elementem są customowe serwisy Kafka Stream odpowiedzialne za zbieranie danych z tematów Kafki *temperature* i *humidity*, wyliczanie na ich podstawie średniej kroczącej i publikowanie wyników w tematach *walking-average-temp* oraz *walking-average-humid*. Dane wynikowe są w postaci:
```JSON
{
  "temperature": 42,
  "walking_average": 1.23
}
```
```JSON
{
  "humidity": 69,
  "walking_average": 6.66
}
```

Ostatnim etapem przepływu danych w naszym systemie jest Kafka MongoDB Connector, który pobiera dane z tematów *walking-average-temp* oraz *walking-average-humid* i zapisuje je do bazy danych MongoDB.

### Schemat systemu
Architektura systemu została przedstawiona na poniższym schemacie.
![Cannot load the pictrue!](/docs/architecture.png?raw=true)

---

## How to ...?
1. ... start the system
Aby uruchomić system wystarczy wpisać w root-directory projektu:
```bash
docker-compose up --build
```

2. ... access MongoDB
Baza danych jest dostępna pod portem 27017. Można ją przeglądnąć na przykład za pomocą MongoDB Compass.
```
mongodb://localhost:27017
```
Dane znajdują się w bazie `weather-statistics` w tabeli `stats`.

---


## Autorzy

Chwała Paweł

Lipiński Łukasz

---
