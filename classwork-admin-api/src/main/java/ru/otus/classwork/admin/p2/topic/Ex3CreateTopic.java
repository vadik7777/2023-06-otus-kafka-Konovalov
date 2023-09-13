package ru.otus.classwork.admin.p2.topic;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import ru.otus.classwork.admin.RemoveAll;
import ru.otus.classwork.admin.Utils;

public class Ex3CreateTopic {

    public static void main(String[] args) {
        Utils.doAdminAction(
                client -> {
                    RemoveAll.removeAll(client);

                    var topics =
                            List.of(
                                    new NewTopic("ex3-topic-1", 1, (short) 1)
                                            .configs(
                                                    Map.of(
                                                            TopicConfig.SEGMENT_MS_CONFIG,
                                                            Integer.toString(1000 * 60 * 60))),
                                    new NewTopic("ex3-topic-2", Optional.empty(), Optional.empty()),
                                    new NewTopic(
                                            "ex3-topic-3", Map.of(0, List.of(1), 1, List.of(2))));

                    var results = client.createTopics(topics);
                    results.all().get();
                });
    }
}
