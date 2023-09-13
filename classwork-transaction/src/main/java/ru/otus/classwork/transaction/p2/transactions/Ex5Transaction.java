package ru.otus.classwork.transaction.p2.transactions;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.otus.classwork.transaction.utils.Utils;
import ru.otus.classwork.transaction.utils.LoggingConsumer;

public class Ex5Transaction {
    private static final String TOPIC1 = "topic1";
    private static final String TOPIC2 = "topic2";

    @SuppressWarnings({"try", "java:S1602"})
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, TOPIC1, TOPIC2);

        try (var producer =
                        new KafkaProducer<String, String>(
                                Utils.createProducerConfig(
                                        b -> {
                                            b.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "ex51");
                                        }));
                var consumer = new LoggingConsumer("1", TOPIC2, Utils.consumerConfig(), true)) {

            producer.initTransactions();

            for (int i = 0; i < 4; ++i) {
                producer.beginTransaction();
                producer.send(new ProducerRecord<>(TOPIC1, "some-" + i));
                producer.send(new ProducerRecord<>(TOPIC2, "other-" + i));
                producer.commitTransaction();
            }

            Thread.sleep(5000);
        }
    }
}
