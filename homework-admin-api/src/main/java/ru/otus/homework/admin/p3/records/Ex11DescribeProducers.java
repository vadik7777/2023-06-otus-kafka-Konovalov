package ru.otus.homework.admin.p3.records;

import java.util.List;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import ru.otus.homework.admin.RemoveAll;
import ru.otus.homework.admin.Utils;

public class Ex11DescribeProducers {

    public static void main(String[] args) {
        Utils.doAdminAction(
                client -> {
                    RemoveAll.removeAll(client);

                    client.createTopics(List.of(new NewTopic("ex11", 1, (short) 1))).all().get();

                    Utils.sendMessages(0, 20, "ex11", null);
                    Utils.sendMessages(20, 40, "ex11", null);

                    var info =
                            client.describeProducers(List.of(new TopicPartition("ex11", 0)))
                                    .all()
                                    .get();

                    Utils.log.info("Info\n{}", info);
                });
    }
}
