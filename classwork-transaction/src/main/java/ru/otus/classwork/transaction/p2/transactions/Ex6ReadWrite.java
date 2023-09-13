package ru.otus.classwork.transaction.p2.transactions;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import ru.otus.classwork.transaction.utils.Utils;
import ru.otus.classwork.transaction.utils.AbstractConsumer;
import ru.otus.classwork.transaction.utils.LoggingConsumer;

@SuppressWarnings({"java:S1192", "java:S1602"})
class Transformer extends AbstractConsumer {
    private static final String GROUP_ID = "ex6-transformer-group";

    private final KafkaProducer<String, String> producer =
            new KafkaProducer<>(
                    Utils.createProducerConfig(
                            b -> {
                                b.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "ex6-transformer");
                            }));

    public Transformer(String name, String topic, Map<String, Object> config) {
        super(name, topic, config);

        producer.initTransactions();

        this.config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        this.config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        this.config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        this.thread.start();
    }

    @Override
    protected void processOne(ConsumerRecord<String, String> consumerRecord) {
        producer.beginTransaction();

        producer.send(
                new ProducerRecord<>(
                        "outbound", consumerRecord.key(), consumerRecord.value() + "-processed"));
        producer.sendOffsetsToTransaction(
                Map.of(
                        new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                        new OffsetAndMetadata(consumerRecord.offset())),
                new ConsumerGroupMetadata(GROUP_ID));
        producer.commitTransaction();
    }

    @Override
    public void close() {
        super.close();
        producer.close();
    }
}

public class Ex6ReadWrite {
    private static final String INBOUND = "inbound";

    @SuppressWarnings({"try", "java:S1192", "java:S1602"})
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, INBOUND, "outbound");

        try (var producer = new KafkaProducer<String, String>(Utils.producerConfig());
                var consumer = new LoggingConsumer("1", "outbound", Utils.consumerConfig(), true);
                var transformer = new Transformer("transformer", INBOUND, Utils.consumerConfig())) {

            producer.send(new ProducerRecord<>(INBOUND, "first-"));
            producer.send(new ProducerRecord<>(INBOUND, "second-"));

            Thread.sleep(10000);
        }
    }
}
