import java.time.LocalDate
import java.time.YearMonth

object adt {

  object tuples {

    /** Tuples ()
      */
    type ProductUnitBoolean = (Unit, Boolean)

    /** Создать возможные экземпляры с типом ProductUnitBoolean
      */

      lazy val p1 = ((), true)
      lazy val p2 = ((), false)

    /** Реализовать тип Person который будет содержать имя и возраст
      */

    type Person = (String, Int)


    /**  Реализовать тип `CreditCard` который может содержать номер (String),
      *  дату окончания (java.time.YearMonth), имя (String), код безопастности (Short)
      */

    type CreditCard = (String, YearMonth, String, Short)

    lazy val cc: CreditCard = ???

  }

  object case_classes {

    /** Case classes
      */


    //  Реализовать Person с помощью case класса

    case class Person(name: String, age: Int)

    // Создать экземпляр для Tony Stark 42 года
    lazy val tonyStark = Person("Tony", 45)


    // Создать case class для кредитной карты


    lazy val cc = ???

  }



  object either {

    /** Sum
      */

    /** Either - это наиболее общий способ хранить один из двух или более кусочков информации в одно время.
      * Также как и кортежи обладает целым рядом полезных методов
      * Иммутабелен
      */

    // trait Ether
    // Right() extends Either
    // Left() extends Either

    type IntOrString = Either[Int, String]
    type BooleanOrUnit = Either[Boolean, Unit]

    val v1: BooleanOrUnit = Left(true)
    val v2: BooleanOrUnit = Left(false)
    val v3: BooleanOrUnit = Right(())

    /** Реализовать экземпляр типа IntOrString с помощью конструктора Right(_)
      */
    val intOrString = ???


    final case class CreditCard()
    final case class WireTransfer()
    final case class Cash()

    /** \
      * Реализовать тип PaymentMethod который может быть представлен одной из альтернатив
      */
    type PaymentMethod = Either[CreditCard, Either[WireTransfer, Cash]]

  }

  object sealed_traits {

    /** Также Sum type можно представить в виде sealed trait с набором альтернатив
      */
    sealed trait PaymentMethod
    final case class CreditCard() extends PaymentMethod
    final case class WireTransfer() extends PaymentMethod
    final case class Cash() extends PaymentMethod


    val pm: PaymentMethod = ???

  }

  object cards {

    sealed trait Suit

    case object Clubs extends Suit
    case object Diamonds extends Suit
    case object Spades extends Suit
    case object Hearts extends Suit

    sealed trait Rank

    case object Two extends Rank
    case object Three extends Rank
    case object Four extends Rank
    case object Five extends Rank
    case object Six extends Rank
    case object Seven extends Rank
    case object Eight extends Rank
    case object Nine extends Rank
    case object Ten extends Rank
    case object Jack extends Rank
    case object Queen extends Rank
    case object King extends Rank
    case object Ace extends Rank

    case class Card(suit: Suit, rank: Rank)


    type Deck = Set[Card]
    type Hand = Set[Card]

    case class Player(hand: Hand, name: String)

    case class Game(deck: Deck, players: Set[Player])

    type PickupCard = (Hand, Card) => Card

  }

}
