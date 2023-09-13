package ru.otus.classwork.producer;

import java.time.Duration;
import java.util.Objects;
import java.util.Properties;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

@SuppressWarnings({"java:S106", "java:S125"})
public class KafkaProducerExample {

    private KafkaProducerExample() {}

    public static final String TOPIC_NAME = "students";

    public static void getKafkaExample() {

        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.56.200:29098");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String k1 = "student001";
        String v1 = "{'Name': 'Ivan', 'Surname': 'Ivanov'}";
        ProducerRecord<String, String> record1 = new ProducerRecord<>(TOPIC_NAME, k1, v1);

        String k2 = "student002";
        String v2 = "{'Name': 'Anna', 'Surname': 'Popova'}";
        ProducerRecord<String, String> record2 = new ProducerRecord<>(TOPIC_NAME, k2, v2);

        String k3 = "student003";
        String v3 = "{'Name': 'Petr', 'Surname': 'Petrov'}";
        ProducerRecord<String, String> record3 = new ProducerRecord<>(TOPIC_NAME, k3, v3);

        // Send first message with blocks until completed
        producer.send(record1);

        // Send second message async: callback with lambda expression
        producer.send(
                record2,
                (recordMetadata, e) -> {
                    if (Objects.isNull(e)) {
                        System.out.println(recordMetadata);
                    } else {
                        e.printStackTrace();
                    }
                });

        // Send third message: Async with a callback class
        producer.send(record3, new CustomCallback());

        // Wait for all previously sent messages, then close
        // producer.close();

        // OR Wait for 60 seconds, then close
        producer.close(Duration.ofSeconds(10));
    }
}
