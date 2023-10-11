package ru.otus;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import ru.otus.kafka.Student;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class AvroConsumerExample {

   public static Student readStudent() {

        //1 - Конфигурация
        var props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://192.168.56.200:29098");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "students");
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://192.168.56.200:9091");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //2 - Создать Producer с соответствующими типами key и value
        var consumer = new KafkaConsumer<String, Student>(props);

        //3 - Подписаться на топик students
        consumer.subscribe(Collections.singleton("students"));

        //4 Прочитать сообщение
        while(true) {
            ConsumerRecords<String, Student> records = consumer.poll(Duration.ofMillis(100));
            for(ConsumerRecord<String, Student> record: records) {
                System.out.printf("Key: %s, Value: %s\n", record.key(), record.value().toString());
            }
        }

    }
}
