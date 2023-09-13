package ru.otus.homework.transaction;

import java.util.List;
import java.util.stream.Stream;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;

public class AdminClient {

    private AdminClient() {}

    @SuppressWarnings({"java:S112", "java:S2142"})
    public static void recreateTopics(String... topics) {
        try (var admin = Admin.create(Config.getAdminConfig())) {
            admin.deleteTopics(List.of(topics)).all().get();
            admin.createTopics(
                    Stream.of(topics).map(topic -> new NewTopic(topic, 1, (short) 1)).toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
