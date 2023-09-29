package zio

import zio.kafka.consumer.{Consumer, ConsumerSettings, Subscription}
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.Serde
import zio.stream.ZStream

object MainApp extends ZIOAppDefault {

  val producer = ZStream.repeatZIO(Random.nextIntBetween(0, Int.MaxValue))
    .schedule(Schedule.fixed(2.seconds))
    .mapZIO{random =>
      Producer.produce[Any, Long, String](
        topic = "random",
        key = random % 4,
        value = random.toString,
        keySerializer = Serde.long,
        valueSerializer = Serde.string
      )
    }
    .drain

  val comnsumer = Consumer.plainStream(Subscription.topics("random"), Serde.long, Serde.string)
    .tap(r => Console.printLine(r.value))
    .map(_.offset)
    .aggregateAsync(Consumer.offsetBatches)
    .mapZIO(_.commit)
    .drain


  def producerLayer = ZLayer.scoped(
    Producer.make(
      settings = ProducerSettings(List("192.168.56.200:29098"))
    )
  )

  def consumerLayer = ZLayer.scoped(
    Consumer.make(
      settings = ConsumerSettings(List("192.168.56.200:29098")).withGroupId("group")
    )
  )

  override def run = producer.merge(comnsumer)
    .runDrain
    .provide(producerLayer, consumerLayer)
}
