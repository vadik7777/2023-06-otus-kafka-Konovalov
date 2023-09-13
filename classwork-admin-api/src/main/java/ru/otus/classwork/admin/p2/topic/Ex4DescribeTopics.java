package ru.otus.classwork.admin.p2.topic;

import java.util.List;
import org.apache.kafka.clients.admin.DescribeTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import ru.otus.classwork.admin.Utils;

public class Ex4DescribeTopics {

    public static void main(String[] args) {
        Utils.doAdminAction(
                client -> {
                    var result =
                            client.describeTopics(
                                            List.of("ex3-topic-1", "ex3-topic-2", "ex3-topic-3"),
                                            new DescribeTopicsOptions()
                                                    .includeAuthorizedOperations(true))
                                    .allTopicNames()
                                    .get();

                    result.forEach(
                            (name, description) -> Utils.log.info("{}: {}", name, description));

                    var topics =
                            client.listTopics(new ListTopicsOptions().listInternal(true))
                                    .names()
                                    .get();
                    Utils.log.info("All topics: {}", topics);
                });
    }
}
