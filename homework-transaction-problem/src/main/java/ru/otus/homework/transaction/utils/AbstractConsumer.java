package ru.otus.homework.transaction.utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public abstract class AbstractConsumer implements AutoCloseable {
    protected final Thread thread = new Thread(this::process);
    private final String topic;
    protected final Map<String, Object> config;
    protected final String name;
    private boolean signalStop = false;

    protected AbstractConsumer(String name, String topic, Map<String, Object> config) {
        this.topic = topic;
        this.name = name;

        this.config = new HashMap<>(config);

        thread.setName("MessagesReceiver." + name);
        thread.setDaemon(true);
    }

    protected void onStartProcess() {
        Utils.log.info("Start!");
    }

    protected void onFinishProcess() {
        Utils.log.info("Complete!");
    }

    protected abstract void processOne(ConsumerRecord<String, String> consumerRecord);

    private void process() {
        onStartProcess();

        try (var consumer = new KafkaConsumer<String, String>(config)) {
            consumer.subscribe(List.of(topic));
            Utils.log.info("Subscribed");

            while (!signalStop) {
                var read = consumer.poll(Duration.ofSeconds(1));
                for (var consumerRecord : read) {
                    processOne(consumerRecord);
                }
            }
        }

        onFinishProcess();
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
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
