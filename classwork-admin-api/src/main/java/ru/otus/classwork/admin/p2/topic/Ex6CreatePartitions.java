package ru.otus.classwork.admin.p2.topic;

import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import ru.otus.classwork.admin.RemoveAll;
import ru.otus.classwork.admin.Utils;

public class Ex6CreatePartitions {

    public static void main(String[] args) {
        Utils.doAdminAction(
                client -> {
                    RemoveAll.removeAll(client);

                    client.createTopics(List.of(new NewTopic("ex6", 1, (short) 1))).all().get();

                    client.createPartitions(Map.of("ex6", NewPartitions.increaseTo(3)));
                });
    }
}
