package ru.otus.classwork.transaction.p1.idempotent.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import ru.otus.classwork.transaction.utils.Utils;
import ru.otus.classwork.transaction.utils.ConsumerSequential;
import ru.otus.classwork.transaction.utils.Producer;

public class Ex2Idempotent {

    private static final String TOPIC = "topic1";

    @SuppressWarnings({"try", "unused"})
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, TOPIC);

        try (var consumer = new ConsumerSequential("rec1", TOPIC, Utils.consumerConfig());
                var producer =
                        new Producer(
                                TOPIC,
                                Utils.createProducerConfig(
                                        b -> b.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)),
                                consumer.lastReceive)) {

            producer.start();

            // Несколько раз убиваем tcp-коннект на порт 9092 (например
            // https://www.nirsoft.net/utils/cports.html)
            Thread.sleep(600000);
        }
    }
}
