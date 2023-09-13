package ru.otus.classwork.transaction;

import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

@SuppressWarnings("java:S1171")
public class Config {

    private Config() {}

    private static final String BOOTSTRAP_SERVERS_CONFIG = "localhost:29098";

    public static Map<String, Object> getAdminConfig() {
        return Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_CONFIG);
    }

    public static Map<String, Object> getProducerConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS_CONFIG,
                ProducerConfig.ACKS_CONFIG,
                "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
    }

    public static Map<String, Object> getProducerTransactionalConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS_CONFIG,
                ProducerConfig.TRANSACTIONAL_ID_CONFIG,
                "transactionalId",
                ProducerConfig.ACKS_CONFIG,
                "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
    }

    public static Map<String, Object> getConsumerReadCommittedConfig() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS_CONFIG,
                ConsumerConfig.GROUP_ID_CONFIG,
                "groupId-" + UUID.randomUUID(),
                ConsumerConfig.ISOLATION_LEVEL_CONFIG,
                "read_committed",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest");
    }

    public static Map<String, Object> getConsumerReadUnCommittedConfig() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS_CONFIG,
                ConsumerConfig.GROUP_ID_CONFIG,
                "groupId-" + UUID.randomUUID(),
                ConsumerConfig.ISOLATION_LEVEL_CONFIG,
                "read_uncommitted",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest");
    }
}
