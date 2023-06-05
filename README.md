# Using Docker for the management of microservices: Kafka, MongoDB, MQTT broker

## Cel projektu
Celem projektu było stworzenie systemu opartego na mikroserwisach zarządzanych Dockerem. Mieliśmy przy tym użyć poniższych technologii:
* Kafka
* MongoDB
* MQTT broker (Mosquitto :mosquito:	)

---


## Mikroserwisy
Nasz projekt składa się z kilku mikroserwisów:
1. Mosquitto broker - odpowiedzialny za zarządzanie kolejkami wiadomości wymienianch w systemie.
2. Weather service temperature - serwis mockujący odczyty z sensora temperatury generujący dane.
3. Weather service humidity - serwis mockujący odczyty z sensora wilgotności generujący dane.
4. Zookeeper - serwis konfuguracyjny i synchronizujący instancje Kafki.
5. Kafka - centrum całego systemu umożliwiający komunikację i wymianę danych pomiędzy serwisami:
5. Kafka Connector MQTT Temperature - konsumer wskazań temperatury z tematu Mosquitto *temperature*
5. Kafka Connector MQTT Humidity - konsumer wskazań wilgotności z tematu Mosquitto *humidity*
6. Kafka Streams Walking Average Temperature - stateful serwis wyliczający średnią krokową temperatury
6. Kafka Streams Walking Average Humidity - stateful serwis wyliczający średnią krokową wilgotności
7. Kafka Connector MongoDB - producent encji do bazy danych
8. MongoDB - instancja bazy danych temperatury, wilgotności i odpowiadającym im średnim krokowym.
9. Nosqlclient - open source web-client bazy MongoDB.

---


## Architektura
### System
Centrum systemu jest serwer Kafki obsługujący dwie instancje Kafka Connect oraz jedną customową instancję Kafka Streams. Niezależnie od Kafki w systemie znajdują się jeszcze: baza danych MongoDB oraz broker MQTT Mosquitto. Każdy z mikroserwisów działa w osobnym kontenerze Dockera i wystawia na zewnątrz tylko kilka portów, głównie do podglądu zgromadzonych danych.

### Docker
Do obsługi systemu został stworzony plik [docker-compose.yml](/docker-compose.yml), który oprócz mikroserwisów startuje również kontener *system-setup*, która czekając na uruchomienie kolejnych serwisów tworzy potrzebne tematy i connectory.

Dodatkowo każdy z systemów czeka na uruchomienie i pełną gotowość serwisów od których zależy. Hierarchia zależności wygląda następująco:
- Mosquitto broker
  - Weather service Temperature
  - Weather service Humidity
  - Kafka
  - *system-setup*
- Zookeeper
  - Kafka
    - Kafka Connector MQTT Temperature
      - *system-setup*
    - Kafka Connector MQTT Humidity
      - *system-setup*
    - Kafka Streams Walking Average Temperature
    - Kafka Streams Walking Average Humidity
    - Kafka Connector MongoDB
      - *system-setup*
- MongoDB
  - Nosqlclient
  - Kafka Connector MongoDB

### Tematy
W systemie obsługujemy tylko 3 tematy podzielone według wystawiającego je brokera:
* Mosquitto:
  - temperature
  - humidity
* Kafka:
  - temperature
  - humidity
  - walking-average-temp
  - walking-average-humid

### Data flow
Przepływ danych zaczyna się od sensorów temperatury i wilgotności. Są one producentami danych Mosquitto i publikują wygenerowane dane w tematach *temperature* i *humidity*. Są to proste dokumenty JSON z jednym tylko polem *temperature* albo *humidity* i wartościami z przedziału [-10, 30]°C albo [0, 100]%. Do generowania odczytów z sensorów służą skrypty [temp.sh](/scripts/temp.sh) i [humid.sh](/scripts/humid.sh). Na przykład:
```JSON
{
  "temperature": 10
}
```
```JSON
{
  "humidity": 42
}
```

Następnie dane z tematów *temperature* i *humidity* są konsumowane przez Kafka Connector MQTT, który jedyne co robi to przekazuje dane z tematu Mosquitto na temat Kafki o identycznej nazwie.

Następnie wiadomości te są konsumowane przez serwisy odpowiadjące za wyliczenie średniej kroczącej - Kafka Streams Walking Average. Zostały tu użyte Kafka Streams DSL (Domain Specific Language), które w zupełności wystarczyły do tak prostego procesowania danych. Tematami Kafki na które publikowane są przetworzone dane są *walking-average-temp* i *walking-average-humid*. Przykładowe dane:
```JSON
{
  "temperature": 10,
  "walking_average": 0.12
}
```
```JSON
{
  "humidity": 42,
  "walking_average": 0.12
}
```

Ostatni etap przepływu danych w naszym systemie jest obsługiwany przez kolejny connector Kafki czytający dane z tematów *walking-average-temp* i *walking-average-humid* i dodający je do bazy danych.

*Plugin Kafki do obsługi MongoDB ma całkiem spore możliwości. Według przeprowadzonego przez nas researchu, możliwe jest wyliczanie średniej kroczącej jako część procesu dodawania danych do bazy.*

### Schemat
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
Dostęp do bazy danych jest umożliwiony poprzez wystawienie na zewnątrz portu `3000` serwisu *nosqlclient*, a więć najprawdopodobniej [tu](http://localhost:3000). Aby połączyć się z bazą danych trzeba dodać połączenie użhywając poniższego adresu.
```
mongodb://mongo-db:27017
```
Dane znajdują się w bazie `weather-statistics` w tabelach `temperature-stats` i `humidity-stats`.

---


## Autorzy

Chwała Paweł

Lipiński Łukasz

---
