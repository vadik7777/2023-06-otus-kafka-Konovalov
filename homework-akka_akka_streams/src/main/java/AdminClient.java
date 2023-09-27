import com.typesafe.config.ConfigFactory;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdminClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminClient.class);
    private static final String CONFIG_PATH = "akka.kafka.admin";

    private AdminClient() {
    }

    public static void recreateTopics(String... topics) {
        var config = ConfigFactory.load();
        var adminConfig = config.getConfig(CONFIG_PATH);
        var adminConfigMap = adminConfig.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> entry.getValue().unwrapped())
                );
        try (var admin = Admin.create(adminConfigMap)) {
            try {
                admin.deleteTopics(List.of(topics)).all().get();
            } catch (Exception e) {
                LOGGER.error("Error delete topics: {}, {}", topics, e.getMessage());
            }
            admin.createTopics(Stream.of(topics).map(topic -> new NewTopic(topic, 1, (short) 1)).toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
