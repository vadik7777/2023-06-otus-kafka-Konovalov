package ru.otus.classwork.admin.p1.create.topic;

import java.util.List;
import org.apache.kafka.clients.admin.NewTopic;
import ru.otus.classwork.admin.Utils;

public class Ex2CreateTopic {

    public static void main(String[] args) {
        Utils.doAdminAction(
                client -> {
                    var requestedTopic = new NewTopic("ex2-topic", 2, (short) 1);

                    var topicResult = client.createTopics(List.of(requestedTopic));
                    // не факт, что топик уже создан, пользоваться им нельзя

                    topicResult.all().get();
                    // топик точно создан, можете пользоваться
                });
    }
}
