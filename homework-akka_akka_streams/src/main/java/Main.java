import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ClosedShape;
import akka.stream.javadsl.*;
import akka.stream.scaladsl.ZipWith3;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String TOPIC = "test";
    private static final String CONFIG_PATH = "akka.kafka.consumer";

    public static void main(String[] args) {

        AdminClient.recreateTopics(TOPIC);
        //ConsumerAlpAkka.consume(TOPIC);
        ProducerAlpAkka.produce(TOPIC);


        var config = ConfigFactory.load();
        var consumerConfig = config.getConfig(CONFIG_PATH);
        var integerDeserializer = new IntegerDeserializer();
        var consumerSettings = ConsumerSettings.create(consumerConfig, integerDeserializer, integerDeserializer);

        var graph = GraphDSL.create((builder) -> {

            var input = builder.add(Consumer.plainSource(consumerSettings, Subscriptions.topics(TOPIC)));
            var mapToValue = builder.add(Flow.of(ConsumerRecord.class).map(record -> (Integer) record.value()));
            var multiplier2 = builder.add(Flow.of(Integer.class).map(elem -> elem * 2));
            var multiplier3 = builder.add(Flow.of(Integer.class).map(elem -> elem * 3));
            var multiplier10 = builder.add(Flow.of(Integer.class).map(elem -> elem * 10));
            var broadcast3 = builder.add(Broadcast.create(Integer.class, 3));
            var zip3 = builder.add(new ZipWith3<Integer, Integer, Integer, Integer>((a, b, c) -> a + b + c));
            var output = builder.add(Sink.foreach(elem -> LOGGER.info(elem.toString())));

            builder.from(input).via(mapToValue).viaFanOut(broadcast3);
            builder.from(broadcast3.out(0)).via(multiplier2).toInlet(zip3.in0());
            builder.from(broadcast3.out(1)).via(multiplier3).toInlet(zip3.in1());
            builder.from(broadcast3.out(2)).via(multiplier10).toInlet(zip3.in2());
            builder.from(zip3.out()).to(output);

            return ClosedShape.getInstance();
        });

        RunnableGraph.fromGraph(graph).run(ActorSystem.create("main-sys"));
    }
}
