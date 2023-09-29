package zio

import zio.stream.{ZSink, ZStream}
import zio.Console.printLine

import scala.concurrent.TimeoutException

object zioStreamsIntroduction extends App {
  val emptyStream = ZStream.empty
  val oneIntValue = ZStream.succeed(4)
  val oneListValueStream = ZStream.succeed(List(1,2,3))
  val infiniteIntStream = ZStream.iterate(1)(_+1)

  val stream = ZStream(1,2,3)
  val unit = ZStream.unit
  val never = ZStream.never

  val s1 = ZStream.fromChunk(Chunk(1,2,3))
  val s2 = ZStream.fromChunks(Chunk(1,2,3), Chunk(4,5,6))
  val s3 = ZStream(1,2,3) ++ ZStream.fail("error") ++ ZStream(4,5)
  val s4 = ZStream(1,2,3)

  val stream1 = s3.orElse(s4)

  val s5 = ZStream(1,2,3) ++ ZStream.fail("error") ++ ZStream(4,5)
  val s6 = ZStream(1,2,3)

  val stream2 = s5.catchSome{
    case "error" => s6
    case "error1" => s4
  }

  val number = ZStream(1,2,3) ++
    ZStream.fromZIO(
      Console.print("Enter a number: ") *> Console.readLine
        .flatMap(x=>
          x.toIntOption match {
            case Some(value) => ZIO.succeed(value)
            case None => ZIO.fail("NaN")
          }
        )
    ).retry(Schedule.exponential(1.seconds))

  number.timeoutFail(new TimeoutException)(10.seconds)

  val sum = ZStream(1,2,3,4).run(ZSink.sum)
  val fold = ZStream(1,2,3,4).runFold(0)(_+_)
  val forEach = ZStream(1,2,3,4).foreach(printLine(_))

  val forever = ZStream.iterate(0)(_+1)
  val take = forever.take(5)
  val takeWhile = forever.takeWhile(_<5)
  val takeUntil = forever.takeUntil(_<5)
  val takeRight = forever.takeRight(3)

  val intStream = ZStream.fromIterable(0 to 100)
  val stringStream = intStream.map(_.toString)
  val rangeFilter = ZStream.range(1, 11).filter(_%2==0)
  val forCompr = for {
    i <- ZStream.range(1, 11).take(10)
    if i%2 ==0
  } yield i
  val filterNot = ZStream(1,2,3,4).filterNot(_%2==0)

}
