import akka.Done;
import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.kafka.ProducerSettings;
import akka.kafka.scaladsl.Producer;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ProducerAlpAkka {

    private ProducerAlpAkka() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerAlpAkka.class);
    private static final String CONFIG_PATH = "akka.kafka.producer";

    public static void produce(String topic) {
        var system = ActorSystem.create("producer-sys");
        var materializer = Materializer.createMaterializer(system);
        var dispatcher = system.dispatcher();

        var config = ConfigFactory.load();
        var producerConfig = config.getConfig(CONFIG_PATH);
        var integerSerializer = new IntegerSerializer();
        var producerSettings = ProducerSettings.create(producerConfig, integerSerializer, integerSerializer);

        var result = Source.range(1, 100)
                .map(value -> new ProducerRecord<>(topic, value, value))
                .runWith(Producer.plainSink(producerSettings), materializer);

        var action = new OnComplete<Done>() {
            @Override
            public void onComplete(Throwable failure, Done success) {
                if (Objects.nonNull(success)) {
                    LOGGER.info("Done");
                } else if (Objects.nonNull(failure)) {
                    LOGGER.error("Error: {}", failure.getMessage());
                }
                system.terminate();
            }
        };

        result.onComplete(action, dispatcher);
    }
}
