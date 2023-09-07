package ru.otus.homework.transaction.p1.idempotent.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import ru.otus.homework.transaction.utils.ConsumerSequential;
import ru.otus.homework.transaction.utils.Producer;
import ru.otus.homework.transaction.utils.Utils;

public class Ex1Problem {

    private static final String TOPIC = "topic1";

    @SuppressWarnings({"try", "unused"})
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, TOPIC);

        try (var consumer = new ConsumerSequential("1", TOPIC, Utils.consumerConfig());
                var producer =
                        new Producer(
                                TOPIC,
                                Utils.createProducerConfig(
                                        b ->
                                                b.put(
                                                        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                                                        false)),
                                consumer.lastReceive)) {

            producer.start();

            // Несколько раз убиваем tcp-коннект на порт 9092 (например
            // https://www.nirsoft.net/utils/cports.html)
            Thread.sleep(600000);
        }
    }
}
