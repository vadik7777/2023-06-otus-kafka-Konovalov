package ru.otus.homework.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

@SuppressWarnings({"java:S106"})
public class CustomCallback implements Callback {

    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            e.printStackTrace();
        } else {
            System.out.println(recordMetadata);
        }
    }
}
