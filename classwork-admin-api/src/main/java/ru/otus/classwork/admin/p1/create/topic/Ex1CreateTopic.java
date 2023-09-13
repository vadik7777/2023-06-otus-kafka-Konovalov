package ru.otus.classwork.admin.p1.create.topic;

import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

public class Ex1CreateTopic {

    public static void main(String[] args) throws Exception {
        Map<String, Object> properties =
                Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.56.200:29098");

        try (var client = Admin.create(properties)) {
            var requestedTopic = new NewTopic("ex1-topic", 2, (short) 1);

            var topicResult = client.createTopics(List.of(requestedTopic));
            // не факт, что топик уже создан, пользоваться им нельзя

            topicResult.all().get();
            // топик точно создан, можете пользоваться
        }
    }
}
