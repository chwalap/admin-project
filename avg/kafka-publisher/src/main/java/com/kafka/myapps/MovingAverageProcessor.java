package com.kafka.myapps;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Short.parseShort;

//public class MovingAverage {
//
//    static String input_topic = "temperature";
//    static String output_topic = "temperature-moving-average";
//
//
//    public static void main(String[] args) throws Exception {
//        Properties streamProps = buildStreamsProperties();
//        Topology topology = buildTopology(new StreamsBuilder());
//
//        createTopics();
//
//        final StreamsBuilder builder = new StreamsBuilder();
//
//        final Topology topology = builder.build();
//        final KafkaStreams streams = new KafkaStreams(topology, props);
//
//
//    }
//
//    protected static Properties buildStreamsProperties() {
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "moving-average");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Double().getClass());
//
//        //props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, envProps.getProperty("default.topic.replication.factor"));
//        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, envProps.getProperty("offset.reset.policy"));
//
//        //props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
//
//        return props;
//    }
//
//    private static void createTopics() {
//        Map<String, Object> config = new HashMap<>();
//
//        config.put("bootstrap.servers", "kafka:9092");
//        AdminClient client = AdminClient.create(config);
//
//        List<NewTopic> topics = new ArrayList<>();
//
//        topics.add(new NewTopic(input_topic, 1, (short) 1));
//        topics.add(new NewTopic(output_topic,1, (short) 1);
//
//        client.createTopics(topics);
//        client.close();
//    }
//
//    private static Topology buildTopology(StreamsBuilder builder) {
//
//        KStream<String, String> source = builder.stream(input_topic);
//        source.to(output_topic);
//        KStream<Long, Double> ratingStream = builder.stream(input_topic,
//                Consumed.with(Serdes.Long(), ));
//
//        getRatingAverageTable(ratingStream, avgRatingsTopicName, getCountAndSumSerde(envProps));
//
//        // finish the topology
//        return builder.build();
//    }
//}
//

public class MovingAverageProcessor implements Processor<Long, Double> {

    private KeyValueStore<Long, Double> state;

    private final double alpha;

    public MovingAverageProcessor(double alpha) {

        if((alpha < 0.0) || (alpha > 1.0)) {
            throw new IllegalArgumentException(
                    "Alpha must be between zero and one.");
        }

        this.alpha = alpha;
    
                System.out.println("Processor init!");
    }

    @Override
    public void init(ProcessorContext context) {
                System.out.println("Processor init!");
this.state = context.getStateStore("moving-average");
        context.schedule(Duration.ofSeconds(1), PunctuationType.STREAM_TIME, (long timestamp) -> {
                System.out.println("Processor schedule!");
        this.state = context.getStateStore("moving-average");
            this.state.all().forEachRemaining(
                    kv -> System.out.println(System.currentTimeMillis() +
                            " PUNCTUATE: " + kv.key +
                            ", " + kv.value));

            this.state.all().forEachRemaining(
                    kv -> context.forward(kv.key, kv.value));

            context.commit();
        });

        this.state = context.getStateStore("moving-average");

        this.state.all().forEachRemaining(
                kv -> System.out.println(System.currentTimeMillis() +
                        " INIT: " + kv.key +
                        ", " + kv.value));

    }

    @Override
    public void process(Long key, Double value) {
                System.out.println("Processor process!");
        Double oldValue = this.state.get(key);

        if(oldValue == null) {
            oldValue = 0.0;
        }

        double newAvg = alpha * value + (1 - alpha) * oldValue;

       this.state.put(key, newAvg);
    }

    @Override
    public void close() {
                System.out.println("Processor init!");

        //state.close();
    }
}