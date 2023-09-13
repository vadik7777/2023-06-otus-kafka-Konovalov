package ru.otus.classwork.admin;

import java.time.Duration;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.InterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"java:S2142", "java:S112"})
public class MessageReceiver implements AutoCloseable {
    private final Thread thread = new Thread(this::process);
    private final String consumerGroup;
    private final List<String> topics;
    private final Logger logger;

    public MessageReceiver(String consumerGroup, int no, List<String> topics) {
        this.consumerGroup = consumerGroup;
        this.topics = topics;
        this.logger = LoggerFactory.getLogger("MessagesReceiver." + consumerGroup + "." + no);

        thread.setDaemon(true);
        thread.start();
    }

    private void process() {
        try (var consumer =
                new KafkaConsumer<String, String>(
                        Utils.createConsumerConfig(
                                m -> m.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup)))) {

            consumer.subscribe(topics);
            logger.info("Subscribed");

            try {
                while (true) {
                    var consumerRecords = consumer.poll(Duration.ofSeconds(1));
                    for (var consumerRecord : consumerRecords) {
                        if (consumerRecord == null) {
                            continue;
                        }
                        var topic = consumerRecord.topic();
                        var partition = consumerRecord.partition();
                        var key = consumerRecord.key();
                        logger.info("Receive {}.{}: {}", topic, partition, key);
                    }
                }
            } catch (InterruptException ignored) {
                Thread.interrupted();
            }
        }
        logger.info("Complete!");
    }

    @Override
    public void close() {
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
