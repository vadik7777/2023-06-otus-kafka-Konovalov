package akka_akka_streams.AkkaActors

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}

object MutableState extends App{
  sealed trait  Command

  case class Deposite(v: Integer) extends Command
  case class Withdraw(v:Int) extends Command
  case class Get() extends Command

  object Account{

    def apply(am: Int): Behavior[Command] = Behaviors.setup{ctx =>
      var amount: Int = am

      Behaviors.receiveMessage{
        case Deposite(v) =>
          amount = amount + v
          ctx.log.info(s"Deposite $v to amount $amount. Total state is $amount")
          Behaviors.same
        case Withdraw(v) =>
          amount = amount - v
          ctx.log.info(s"Withdrow $v from amount $amount. Total state is $amount")
          Behaviors.same
        case Get() =>
          ctx.log.info(s"Total state is $amount")
          Behaviors.same
      }
    }
  }

  def apply():Behavior[NotUsed] =
    Behaviors.setup{ctx =>
      val account1 = ctx.spawn(Account(2000), "actor_1")
      val account2 = ctx.spawn(Account(42), "actor_2")


      account1 ! Get()
      account2 ! Get()

      account1 ! Deposite(1)
      account2 ! Get()
      account1 ! Deposite(1)

      for (_ <- 1 to 10) {
        account1 ! Withdraw(1)
      }
      Behaviors.same
    }

  implicit val system = ActorSystem(MutableState(), "akka_typed")
  Thread.sleep(5000)
  system.terminate()


}