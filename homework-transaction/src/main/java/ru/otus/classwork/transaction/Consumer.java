package ru.otus.classwork.transaction;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer implements AutoCloseable {
    public static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    private boolean signalStop = false;
    private final Thread thread;

    protected Consumer(String name, Map<String, Object> config, String... topics) {
        thread = new Thread(() -> process(name, config, topics));
        thread.setName(name);
        thread.setDaemon(true);
        thread.start();
    }

    @SuppressWarnings("java:S2629")
    private void process(String name, Map<String, Object> config, String... topics) {
        LOGGER.info("start Consumer: {}", name);

        try (var consumer = new KafkaConsumer<String, String>(config)) {
            consumer.subscribe(List.of(topics));
            LOGGER.info("subscribed Consumer: {}", name);

            while (!signalStop) {
                var read = consumer.poll(Duration.ofSeconds(1));
                for (var consumerRecord : read) {
                    LOGGER.info(
                            "receive Consumer: {} - {} at {}",
                            name,
                            consumerRecord.value(),
                            consumerRecord.offset());
                }
            }
            LOGGER.info("stop Consumer: {}", name);
        }
    }

    @SuppressWarnings({"java:S2142", "java:S112"})
    @Override
    public void close() {
        this.signalStop = true;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
