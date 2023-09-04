package ru.otus.homework.admin.p3.records;

import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.common.TopicPartition;
import ru.otus.homework.admin.RemoveAll;
import ru.otus.homework.admin.Utils;

public class Ex9DeleteRecords {

    public static void main(String[] args) {
        Utils.doAdminAction(
                client -> {
                    RemoveAll.removeAll(client);

                    client.createTopics(List.of(new NewTopic("ex9", 2, (short) 1))).all().get();

                    Utils.sendMessages(0, 500, "ex9", 0);

                    client.deleteRecords(
                                    Map.of(
                                            new TopicPartition("ex9", 0),
                                            RecordsToDelete.beforeOffset(250)))
                            .all()
                            .get();
                });
    }
}
