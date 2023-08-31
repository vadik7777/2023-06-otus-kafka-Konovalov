package ru.otus.homework.consumer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

@SuppressWarnings({"java:S106"})
public class ConsumerKafka {

    private ConsumerKafka() {}

    public static final String TOPIC_NAME = "students";

    public static void consumerExample() {

        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.56.200:29098");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "myGroup1");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(List.of(TOPIC_NAME));

        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords =
                        consumer.poll(Duration.ofMillis(10000));
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords)
                    System.out.printf(
                            "offset = %d, key = %s, value = %s%n",
                            consumerRecord.offset(), consumerRecord.key(), consumerRecord.value());
            }
        } finally {
            consumer.close();
        }
    }
}
