package ru.otus.homework.streams.client;

import org.apache.kafka.clients.admin.NewTopic;
import ru.otus.homework.streams.config.Config;

import java.util.List;
import java.util.stream.Stream;

public class Admin {

    private Admin() {
    }

    public static void recreateTopics(String... topics) {
        try (var admin = org.apache.kafka.clients.admin.Admin.create(Config.getAdminConfig())) {
            try {
                admin.deleteTopics(List.of(topics)).all().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            admin.createTopics(
                    Stream.of(topics)
                            .map(topic -> new NewTopic(topic, 2, (short) 1))
                            .toList()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
