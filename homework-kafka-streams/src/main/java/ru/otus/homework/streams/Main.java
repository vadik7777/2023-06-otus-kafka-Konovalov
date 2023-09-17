package ru.otus.homework.streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.streams.client.Admin;
import ru.otus.homework.streams.client.Producer;
import ru.otus.homework.streams.config.Config;
import ru.otus.homework.streams.model.Model;
import ru.otus.homework.streams.serde.JsonDeserializer;
import ru.otus.homework.streams.serde.JsonSerializer;

import java.time.Duration;
import java.util.*;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final Serde<Model> MODEL_SERDE =
            new Serdes.WrapperSerde<>(new JsonSerializer<>(), new JsonDeserializer<>(Model.class));
    private static final Serde<Integer> INTEGER_SERDE = Serdes.Integer();
    private static final String TOPIC = "homework-kafka-streams";
    private static final String PRODUCER_NAME = "producer1";
    private static final Duration WINDOW_SIZE = Duration.ofMinutes(5);
    private static final int MESSAGE_COUNT = 600;
    private static final long MILLI_SECONDS_INTERVAL = 1000;
    private static final Map<String, Object> PRODUCER_CONFIG = Config.getProducerConfig();
    private static final Map<String, Object> STREAM_CONFIG = Config.getStreamConfig();


    public static void main(String[] args) {

        var builder = new StreamsBuilder();
        builder.stream(TOPIC, Consumed.with(INTEGER_SERDE, MODEL_SERDE))
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(WINDOW_SIZE))
                .emitStrategy(EmitStrategy.onWindowClose())
                .count()
                .toStream()
                .foreach((w, c) -> LOGGER.info("key: {}, count: {}", w.key(), c));


        LOGGER.info("{}", builder.build().describe());

        Admin.recreateTopics(TOPIC);

        try (
                var kafkaStreams = new KafkaStreams(builder.build(), new StreamsConfig(STREAM_CONFIG))
        ) {
            LOGGER.info("App Started");

            kafkaStreams.start();

            var producer = new Producer(PRODUCER_NAME, PRODUCER_CONFIG, MESSAGE_COUNT, MILLI_SECONDS_INTERVAL, TOPIC);
            var producerThread = producer.start();
            producerThread.start();
            producerThread.join();

            LOGGER.info("Shutting down now");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
