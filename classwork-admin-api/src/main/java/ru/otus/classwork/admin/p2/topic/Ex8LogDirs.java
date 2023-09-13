package ru.otus.classwork.admin.p2.topic;

import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartitionReplica;
import ru.otus.classwork.admin.RemoveAll;
import ru.otus.classwork.admin.Utils;

@SuppressWarnings({"java:S1192", "java:S125"})
public class Ex8LogDirs {

    public static void main(String[] args) {
        Utils.doAdminAction(
                client -> {
                    RemoveAll.removeAll(client);

                    client.createTopics(List.of(new NewTopic("ex8-1", Map.of(0, List.of(1, 2)))));

                    // пошлем немного сообщений
                    // Utils.sendMessages(0, 500, "ex8-1", null);

                    var res = client.describeLogDirs(List.of(1, 2)).allDescriptions().get();
                    Utils.log.info("Res: {}", res);

                    var res2 =
                            client.describeReplicaLogDirs(
                                            List.of(new TopicPartitionReplica("ex8-1", 0, 2)))
                                    .all()
                                    .get();
                    Utils.log.info("Res: {}", res2);
                });
    }
}
