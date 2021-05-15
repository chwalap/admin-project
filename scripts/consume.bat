call docker run -it --rm --network admin-project_default confluentinc/cp-kafka:5.1.0 ^
kafka-console-consumer --bootstrap-server kafka:9092 --topic asdf --from-beginning
