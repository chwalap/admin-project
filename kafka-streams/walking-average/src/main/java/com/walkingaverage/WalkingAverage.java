package com.walkingaverage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

public class WalkingAverage {

  static final String APPLICATION_ID = "walking-average-stream";
  static final String CLIENT_ID = "client-stream";
  static final String BOOTSTRAP_SERVERS = "kafka:9092";
  static final String TEMPERATURE_TOPIC = "temperature";
  static final String WALKING_AVERAGE_TOPIC = "walking-average";

  public static void main(final String[] args) {

    final Properties streamsConfiguration = new Properties();
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
    streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, CLIENT_ID);
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Double().getClass().getName());
    streamsConfiguration.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
        LogAndContinueExceptionHandler.class);
    streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
    streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);

    final Topology topology = getTopology();
    final KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);

    streams.cleanUp();
    streams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
  }

  static Topology getTopology() {
    Map<String, Object> serdeProps = new HashMap<>();

    final Serializer<CountAndSum> countAndSumSerializer = new JsonPOJOSerializer<>();
    serdeProps.put("JsonPOJOClass", CountAndSum.class);
    countAndSumSerializer.configure(serdeProps, false);
    final Deserializer<CountAndSum> countAndSumDeserializer = new JsonPOJODeserializer<>();
    serdeProps.put("JsonPOJOClass", CountAndSum.class);
    countAndSumDeserializer.configure(serdeProps, false);
    final Serde<CountAndSum> countAndSumSerde = Serdes.serdeFrom(countAndSumSerializer, countAndSumDeserializer);

    final Serializer<Temperature> temperatureSerializer = new JsonPOJOSerializer<>();
    serdeProps.put("JsonPOJOClass", Temperature.class);
    temperatureSerializer.configure(serdeProps, false);
    final Deserializer<Temperature> temperatureDeserializer = new JsonPOJODeserializer<>();
    serdeProps.put("JsonPOJOClass", Temperature.class);
    temperatureDeserializer.configure(serdeProps, false);
    final Serde<Temperature> temperatureSerde = Serdes.serdeFrom(temperatureSerializer, temperatureDeserializer);

    final Serializer<AvgTemperature> avgTemperatureSerializer = new JsonPOJOSerializer<>();
    serdeProps.put("JsonPOJOClass", AvgTemperature.class);
    avgTemperatureSerializer.configure(serdeProps, false);
    final Deserializer<AvgTemperature> avgTemperatureDeserializer = new JsonPOJODeserializer<>();
    serdeProps.put("JsonPOJOClass", AvgTemperature.class);
    avgTemperatureDeserializer.configure(serdeProps, false);
    final Serde<AvgTemperature> avgTemperatureSerde = Serdes.serdeFrom(avgTemperatureSerializer,
        avgTemperatureDeserializer);

    final StreamsBuilder builder = new StreamsBuilder();
    final KStream<String, Temperature> input = builder.stream(TEMPERATURE_TOPIC,
        Consumed.with(Serdes.String(), temperatureSerde));

    KTable<String, AvgTemperature> walkingAverage = input.selectKey((k, v) -> "count_and_sum").groupByKey()
        .aggregate(() -> new CountAndSum(0L, 0.0), (k, v, agg) -> {
          agg.incCount();
          agg.incSum(v.getTemperature());
          return agg;
        }, Materialized.with(Serdes.String(), countAndSumSerde))
        .mapValues(value -> new AvgTemperature(value.getSum() / value.getCount()),
            Materialized.with(Serdes.String(), avgTemperatureSerde));

    walkingAverage.toStream().to(WALKING_AVERAGE_TOPIC, Produced.with(Serdes.String(), avgTemperatureSerde));
    return builder.build();
  }
}