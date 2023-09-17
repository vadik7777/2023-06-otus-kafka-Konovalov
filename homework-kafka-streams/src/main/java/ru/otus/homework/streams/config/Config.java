package ru.otus.homework.streams.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.streams.StreamsConfig;
import ru.otus.homework.streams.serde.JsonSerializer;

import java.util.Map;
import java.util.UUID;

public class Config {

    private Config() {}

    private static final String APP_ID = "appId";
    private static final String HOST = "localhost:29098";

    public static Map<String, Object> getStreamConfig() {
        return Map.of(
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, HOST,
                StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000,
                StreamsConfig.APPLICATION_ID_CONFIG, APP_ID + "-" + UUID.randomUUID()
        );
    }

    public static Map<String, Object> getAdminConfig() {
        return Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);
    }

    public static Map<String, Object> getProducerConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST,
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
    }
}
