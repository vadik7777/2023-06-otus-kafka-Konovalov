import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ConsumerAlpAkka {

    private ConsumerAlpAkka() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerAlpAkka.class);
    private static final String CONFIG_PATH = "akka.kafka.consumer";

    public static void consume(String topic) {
        var system = ActorSystem.create("consumer-sys");
        var materializer = Materializer.createMaterializer(system);

        var config = ConfigFactory.load();
        var consumerConfig = config.getConfig(CONFIG_PATH);
        var integerDeserializer = new IntegerDeserializer();
        var consumerSettings = ConsumerSettings.create(consumerConfig, integerDeserializer, integerDeserializer);

        Consumer.plainSource(consumerSettings, Subscriptions.topics(topic))
                .runWith(Sink.foreach(record -> LOGGER.info(record.toString())), materializer)
                .whenComplete((done, failure) -> {
                    if (Objects.nonNull(done)) {
                        LOGGER.info("Done");
                    }
                    if (Objects.nonNull(failure)) {
                        LOGGER.error("Error: {}", failure.getMessage());
                    }
                    system.terminate();
                });
    }
}
