package com.kafka.myapps;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.Map;
import java.util.Properties;

public class MovingAverage {
    public static void main(String[] args) {

        class IgnoreRecordTooLargeHandler implements ProductionExceptionHandler {
            public void configure(Map<String, ?> config) {}
            public ProductionExceptionHandlerResponse handle(final ProducerRecord<byte[], byte[]> record,
                                                             final Exception exception) {
                if (exception instanceof RecordTooLargeException) {
                    return ProductionExceptionHandlerResponse.CONTINUE;
                } else {
                    return ProductionExceptionHandler.ProductionExceptionHandlerResponse.FAIL;
                }
            }
        }

        class Temperature {
            Long time;
            Long temperature;

            public Long getTime() {
                return time;
            }
        }

        class TemperatureTimeExtractor implements TimestampExtractor {
            @Override
            public long extract(final ConsumerRecord<Object, Object> record, final long previousTimestamp) {
                long timestamp = -1;
                final Temperature myPojo = (Temperature) record.value();
                if (myPojo != null) {
                    timestamp = myPojo.getTime();
                }
                if (timestamp < 0) {
                    if (previousTimestamp >= 0) {
                        return previousTimestamp;
                    } else {
                        return System.currentTimeMillis();
                    }
                }
                return timestamp;
            }

        }

        StoreBuilder averageStoreBuilder =
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore("moving-average"),
                        Serdes.String(),
                        Serdes.Long()
                );

                System.out.println("Builder created!");

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-connect-moving-average");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, IgnoreRecordTooLargeHandler.class);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TemperatureTimeExtractor.class);
                System.out.println("Properties created!");

        Topology builder = new Topology();
        builder.addSource("Source", "temperature")
                .addProcessor("Process", () -> new MovingAverageProcessor(0.5), "Source")
                .addStateStore(averageStoreBuilder, "Process")
                .addSink("Sink", "sink-temperature", "Process");

                
                System.out.println("Topology created!");


        KafkaStreams streams = new KafkaStreams(builder, props);
                System.out.println("Kafka sterams started!");
        streams.start();
    }
}
