package com.walkingaveragehumid;

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
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.LogManager;

public class WalkingAverageHumid {

  private static final Logger logger = LogManager.getLogger(WalkingAverageHumid.class);

  static final String APPLICATION_ID = "walking-average-humid-stream";
  static final String CLIENT_ID = "client-stream-humid";
  static final String BOOTSTRAP_SERVERS = "kafka:9092";
  static final String HUMIDITY_TOPIC = "humidity";
  static final String WALKING_AVERAGE_TOPIC = "walking-average-humid";

  public static void main(final String[] args) {
    PropertyConfigurator.configure(WalkingAverageHumid.class.getClassLoader().getResource("log4j.properties"));

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
    streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);

    logger.info("Creating topology");

    final Topology topology = getTopology();
    final KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);

    streams.cleanUp();
    streams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
  }

  static Topology getTopology() {
    logger.info("Defining serdes");
    Map<String, Object> serdeProps = new HashMap<>();

    final Serializer<CountAndSum> countAndSumSerializer = new JsonPOJOSerializer<>();
    serdeProps.put("JsonPOJOClass", CountAndSum.class);
    countAndSumSerializer.configure(serdeProps, false);
    final Deserializer<CountAndSum> countAndSumDeserializer = new JsonPOJODeserializer<>();
    serdeProps.put("JsonPOJOClass", CountAndSum.class);
    countAndSumDeserializer.configure(serdeProps, false);
    final Serde<CountAndSum> countAndSumSerde = Serdes.serdeFrom(countAndSumSerializer, countAndSumDeserializer);

    final Serializer<Humidity> humiditySerializer = new JsonPOJOSerializer<>();
    serdeProps.put("JsonPOJOClass", Humidity.class);
    humiditySerializer.configure(serdeProps, false);
    final Deserializer<Humidity> humidityDeserializer = new JsonPOJODeserializer<>();
    serdeProps.put("JsonPOJOClass", Humidity.class);
    humidityDeserializer.configure(serdeProps, false);
    final Serde<Humidity> humiditySerde = Serdes.serdeFrom(humiditySerializer, humidityDeserializer);

    final Serializer<AvgHumidity> avgHumiditySerializer = new JsonPOJOSerializer<>();
    serdeProps.put("JsonPOJOClass", AvgHumidity.class);
    avgHumiditySerializer.configure(serdeProps, false);
    final Deserializer<AvgHumidity> avgHumidityDeserializer = new JsonPOJODeserializer<>();
    serdeProps.put("JsonPOJOClass", AvgHumidity.class);
    avgHumidityDeserializer.configure(serdeProps, false);
    final Serde<AvgHumidity> avgHumiditySerde = Serdes.serdeFrom(avgHumiditySerializer,
    avgHumidityDeserializer);

    logger.info("Building stream");
    final StreamsBuilder builder = new StreamsBuilder();
    final KStream<String, Humidity> input = builder.stream(HUMIDITY_TOPIC,
        Consumed.with(Serdes.String(), humiditySerde));

    KTable<String, AvgHumidity> walkingAverage = input.selectKey((k, v) -> "count_and_sum").groupByKey()
        .aggregate(() -> new CountAndSum(0L, 0.0, 0.0), (k, v, agg) -> {
          logger.info("Aggregating... k: " + k + "; v: " + v + "; agg: " + agg);
          agg.incCount();
          agg.incSum(v.getHumidity());
          agg.setLatestValue(v.getHumidity());
          return agg;
        }, Materialized.with(Serdes.String(), countAndSumSerde))
        .mapValues(value -> new AvgHumidity(value.getSum() / value.getCount(), value.getLatestValue()),
            Materialized.with(Serdes.String(), avgHumiditySerde));

    walkingAverage.toStream().to(WALKING_AVERAGE_TOPIC, Produced.with(Serdes.String(), avgHumiditySerde));
    return builder.build();
  }
}
