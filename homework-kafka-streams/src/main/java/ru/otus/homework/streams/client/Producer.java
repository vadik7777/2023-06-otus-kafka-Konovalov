package ru.otus.homework.streams.client;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.streams.model.Model;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class Producer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    private final Thread thread;

    public Producer(String name, Map<String, Object> config, Integer messageCount, Long milliSecondsInterval,
                       String... topics) {
        thread = new Thread(() -> process(name, config, messageCount, milliSecondsInterval, topics));
        thread.setName(name);
        thread.setDaemon(true);
    }

    private void process(String name, Map<String, Object> config, Integer messageCount, Long milliSecondsInterval,
                         String... topics) {
        var random = new Random();
        LOGGER.info("producer start: {}", name);

        try (var producer = new KafkaProducer<Integer, Model>(config)) {
            var messagesSend = 0;
            while (messageCount > messagesSend) {
                messagesSend++;
                var key = random.nextInt(1, 4);
                var model = Model.builder().key(key).value(messagesSend).build();
                Arrays.stream(topics).forEach(
                        topic -> {
                            producer.send(new ProducerRecord<>(topic, key, model));
                            //LOGGER.info("{} send: {}", name, model);
                        }
                );

                try {
                    Thread.sleep(Duration.ofMillis(milliSecondsInterval).toMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            producer.flush();
        }
    }

    public Thread start() {
        return thread;
    }
}
