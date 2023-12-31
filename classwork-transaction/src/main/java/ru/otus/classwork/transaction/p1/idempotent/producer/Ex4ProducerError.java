package ru.otus.classwork.transaction.p1.idempotent.producer;

import ru.otus.classwork.transaction.utils.Utils;
import ru.otus.classwork.transaction.utils.ConsumerSequential;
import ru.otus.classwork.transaction.utils.Producer;

public class Ex4ProducerError {

    private static final String TOPIC = "topic1";

    @SuppressWarnings({"try", "unused"})
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, TOPIC);

        try (var consumer = new ConsumerSequential("1", TOPIC, Utils.consumerConfig());
                var producer1 = new Producer(TOPIC, Utils.producerConfig(), consumer.lastReceive)) {

            producer1.start();
            Thread.sleep(500);

            try (var producer2 =
                    new Producer(
                            TOPIC,
                            Utils.producerConfig(),
                            consumer.lastReceive,
                            producer1.getLastSend() - 10)) {
                producer2.start();
                Thread.sleep(500);
            }

            Thread.sleep(5000);
        }
    }
}
