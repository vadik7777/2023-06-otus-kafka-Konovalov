package akka_akka_streams.AkkaDataStreams

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}

object  BackPressure extends App {
  val sourceFast = Source(1 to 20)
  val sinkSlow = Sink.foreach[Int]{el =>
    Thread.sleep(1000)
    println(s"Sink inside: $el")

  }

  val flow = Flow[Int].map{el=>
    println(s"Flow inside: $el")
    el
  }

  val flowWithBuffer = flow.buffer(1, overflowStrategy = OverflowStrategy.dropHead)

  implicit val system = ActorSystem("fusion")
  implicit  val materializer = ActorMaterializer()


  sourceFast.async
    .via(flow).async
    .to(sinkSlow)
    //.run()

  sourceFast.async
    .via(flowWithBuffer).async
    .to(sinkSlow)
    .run()



}
