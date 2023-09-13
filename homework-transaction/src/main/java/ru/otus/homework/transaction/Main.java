package ru.otus.homework.transaction;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Main {

    private static final String TOPIC_1 = "topic1";
    private static final String TOPIC_2 = "topic2";

    @SuppressWarnings({"try", "java:S125"})
    public static void main(String[] args) throws InterruptedException {
        try (var producer =
                        new KafkaProducer<String, String>(Config.getProducerTransactionalConfig());
                var consumerCommitted =
                        new Consumer(
                                "committed  ",
                                Config.getConsumerReadCommittedConfig(),
                                TOPIC_1,
                                TOPIC_2);
                /*var consumerUncommitted =
                new Consumer(
                        "uncommitted",
                        Config.getConsumerReadUnCommittedConfig(),
                        TOPIC_1,
                        TOPIC_2); */ ) {

            AdminClient.recreateTopics(TOPIC_1, TOPIC_2);

            producer.initTransactions();
            producer.beginTransaction();

            producer.send(new ProducerRecord<>(TOPIC_1, "topic1_one"));
            producer.send(new ProducerRecord<>(TOPIC_1, "topic1_two"));
            producer.send(new ProducerRecord<>(TOPIC_1, "topic1_three"));
            producer.send(new ProducerRecord<>(TOPIC_1, "topic1_four"));
            producer.send(new ProducerRecord<>(TOPIC_1, "topic1_five"));

            producer.send(new ProducerRecord<>(TOPIC_2, "topic2_one"));
            producer.send(new ProducerRecord<>(TOPIC_2, "topic2_two"));
            producer.send(new ProducerRecord<>(TOPIC_2, "topic2_three"));
            producer.send(new ProducerRecord<>(TOPIC_2, "topic2_four"));
            producer.send(new ProducerRecord<>(TOPIC_2, "topic2_five"));

            producer.commitTransaction();
            producer.beginTransaction();

            producer.send(new ProducerRecord<>(TOPIC_1, "topic1_six"));
            producer.send(new ProducerRecord<>(TOPIC_1, "topic1_seven"));

            producer.send(new ProducerRecord<>(TOPIC_2, "topic2_six"));
            producer.send(new ProducerRecord<>(TOPIC_2, "topic2_seven"));

            // Без этого не отправляются в кафка абортнутые сообщения
            Thread.sleep(100);

            producer.abortTransaction();
        }
    }
}
