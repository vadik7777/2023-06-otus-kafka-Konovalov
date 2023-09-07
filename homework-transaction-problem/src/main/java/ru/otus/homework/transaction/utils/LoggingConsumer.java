package ru.otus.homework.transaction.utils;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class LoggingConsumer extends AbstractConsumer {
    public LoggingConsumer(
            String name, String topic, Map<String, Object> config, boolean readCommitted) {
        super(name, topic, config);

        this.config.put(ConsumerConfig.GROUP_ID_CONFIG, name);
        this.config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        if (readCommitted) {
            this.config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        }

        this.thread.start();
    }

    @SuppressWarnings("java:S2629")
    @Override
    protected void processOne(ConsumerRecord<String, String> consumerRecord) {
        Utils.log.info(
                "Receive {}:{} at {}",
                consumerRecord.key(),
                consumerRecord.value(),
                consumerRecord.offset());
    }
}
