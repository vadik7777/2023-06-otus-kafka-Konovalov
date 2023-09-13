package ru.otus.classwork.admin.p2.topic;

import java.util.List;
import org.apache.kafka.clients.admin.NewTopic;
import ru.otus.classwork.admin.RemoveAll;
import ru.otus.classwork.admin.Utils;

public class Ex5DeleteTopic {

    public static void main(String[] args) {
        Utils.doAdminAction(
                client -> {
                    RemoveAll.removeAll(client);

                    client.createTopics(List.of(new NewTopic("ex5", 1, (short) 1))).all().get();

                    client.deleteTopics(List.of("ex5")).all().get();

                    client.createTopics(List.of(new NewTopic("ex5", 1, (short) 1))).all().get();
                });
    }
}
