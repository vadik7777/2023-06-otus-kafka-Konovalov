package ru.otus.classwork.transaction.p1.idempotent.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import ru.otus.classwork.transaction.utils.Utils;

public class Ex3InvalidConfig {
    public static void main(String[] args) {
        Utils.recreateTopics(1, 1, "topic1");

        var producer =
                new KafkaProducer<String, String>(
                        Utils.createProducerConfig(
                                b -> {
                                    b.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
                                    b.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 6);
                                }));
        producer.close();
    }
}
