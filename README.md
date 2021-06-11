# Using Docker for the management of microservices: Kafka, MongoDB, MQTT broker

## Cel projektu
Celem projektu było stworzenie systemu opartego na mikroserwisach zarządzanych Dockerem. Mieliśmy przy tym użyć poniższych technologii:
* Kafka
* MongoDB
* MQTT broker (Mosquitto :mosquito:	)

---


## Mikroserwisy
Nasz projekt składa się z dziewięciu mikroserwisów:
1. Mosquitto broker - odpowiedzialny za zarządzanie kolejkami wiadomości wymienianch w systemie.
2. Weather service - serwis mockujący odczyty z sensora temperatury generujący dane.
3. Zookeeper - serwis konfuguracyjny i synchronizujący instancje Kafki.
4. Kafka - centrum całego systemu umożliwiający komunikację i wymianę danych pomiędzy serwisami:
5. Kafka Connector MQTT - konsumer wskazań temperatury z tematu Mosquitto *temperature*
6. Kafka Streams Walking Average - stateful serwis wyliczający średnią krokową temperatury
7. Kafka Connector MongoDB - producent encji do bazy danych
8. MongoDB - instancja bazy danych temperatury i odpowiadającej jej średniej krokowej.
9. Nosqlclient - open source web-client bazy MongoDB.

---


## Architektura
### System
Centrum systemu jest serwer Kafki obsługujący dwie instancje Kafka Connect oraz jedną customową instancję Kafka Streams. Niezależnie od Kafki w systemie znajdują się jeszcze: baza danych MongoDB oraz broker MQTT Mosquitto. Każdy z mikroserwisów działa w osobnym kontenerze Dockera i wystawia na zewnątrz tylko kilka portów, głównie do podglądu zgromadzonych danych.

### Docker
Do obsługi systemu został stworzony plik [docker-compose.yml](/docker-compose.yml), który oprócz mikroserwisów startuje również kontener *system-setup*, która czekając na uruchomienie kolejnych serwisów tworzy potrzebne tematy i connectory.

Dodatkowo każdy z systemów czeka na uruchomienie i pełną gotowość serwisów od których zależy. Hierarchia zależności wygląda następująco:
- Mosquitto broker
  - Weather service
  - Kafka
  - *system-setup*
- Zookeeper
  - Kafka
    - Kafka Connector MQTT
      - *system-setup*
    - Kafka Streams Walking Average
    - Kafka Connector MongoDB
      - *system-setup*
- MongoDB
  - Nosqlclient
  - Kafka Connector MongoDB

### Tematy
W systemie obsługujemy tylko 3 tematy podzielone według wystawiającego je brokera:
* Mosquitto:
  - temperature
* Kafka:
  - temperature
  - walking-average

### Data flow
Przepływ danych zaczyna się od sensora temperatury. Jest on producentem danych Mosquitto i publikuje wygenerowane dane w temacie *temperature*. Jest to prosty dokument JSON z jednym tylko polem *temperature* i wartością z przedziału [-10, 30]°C. Do generowania odczytów z sensora słyży skrypt [temp.sh](/scripts/temp.sh). Na przykład:
```JSON
{
  "temperature": 10
}
```

Następnie dane z tematu *temperature* są konsumowane przez Kafka Connector MQTT, który jedyne co robi to przekazuje dane z tematu Mosquitto na temat Kafki o identycznej nazwie.

Następnie wiadomości te są konsumowane przez serwis odpowiadjący za wyliczenie średniej kroczącej - Kafka Streams Walking Average. Zostały tu użyte Kafka Streams DSL (Domain Specific Language), które w zupełności wystarczyły do tak prostego procesowania danych. Tematem Kafki na który publikowane są przetworzone dane jest *walking-average*. Przykładowe dane:
```JSON
{
  "temperature": 10,
  "walking_average": 0.12
}
```

Ostatni etap przepływu danych w naszym systemie jest obsługiwany przez kolejny connector Kafki czytający dane z tematu *walking-average* i dodający je do bazy danych.

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
Dane znajdują się w bazie `temperature-statistics` w tabeli `stats`.

3. ... contribute to this amazing project
Niestety nie jest to możliwe. Projekt został stworzony w ramach przedmiotu Administracja Systemów Komputerowych.

---


## Autorzy

Chwała Paweł

Kozaczkiewicz Łukasz

---
