package cron4s.testkit

import cron4s._
import cron4s.expr._
import cron4s.spi.DateTimeAdapter
import cron4s.testkit.discipline.NodeDateTimeTests
import cron4s.testkit.gen._

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite

import org.typelevel.discipline.scalatest.Discipline

import scalaz.Equal

/**
  * Created by alonsodomin on 04/08/2016.
  */
abstract class NodeDateTimeTestKit[DateTime <: AnyRef : DateTimeAdapter : Equal]
  extends FunSuite with Discipline with ExtensionsTestKitBase[DateTime] {
  import CronField._
  import CronUnit._

  trait NodeCheck {
    def check(): Unit
  }

  implicit lazy val arbitraryDateTime = Arbitrary(for {
    seconds     <- Gen.choose(Seconds.min, Seconds.max)
    minutes     <- Gen.choose(Minutes.min, Minutes.max)
    hours       <- Gen.choose(Hours.min, Hours.max)
    daysOfMonth <- Gen.choose(DaysOfMonth.min, DaysOfMonth.max)
    months      <- Gen.choose(Months.min, Months.max)
    daysOfWeek  <- Gen.choose(DaysOfWeek.min, DaysOfWeek.max)
  } yield createDateTime(seconds, minutes, hours, daysOfMonth, months, daysOfWeek))

  object each extends NodeCheck with ArbitraryEachNode {
    def check() = {
      checkAll("NodeDateTime[EachNode, Second]", NodeDateTimeTests[EachNode, Second, DateTime].dateTime)
      checkAll("NodeDateTime[EachNode, Minute]", NodeDateTimeTests[EachNode, Minute, DateTime].dateTime)
      checkAll("NodeDateTime[EachNode, Hour]", NodeDateTimeTests[EachNode, Hour, DateTime].dateTime)
      checkAll("NodeDateTime[EachNode, DayOfMonth]", NodeDateTimeTests[EachNode, DayOfMonth, DateTime].dateTime)
      checkAll("NodeDateTime[EachNode, Month]", NodeDateTimeTests[EachNode, Month, DateTime].dateTime)
      checkAll("NodeDateTime[EachNode, DayOfWeek]", NodeDateTimeTests[EachNode, DayOfWeek, DateTime].dateTime)
    }
  }

  object const extends NodeCheck with ArbitraryConstNode {
    def check() = {
      checkAll("NodeDateTime[ConstNode, Second]", NodeDateTimeTests[ConstNode, Second, DateTime].dateTime)
      checkAll("NodeDateTime[ConstNode, Minute]", NodeDateTimeTests[ConstNode, Minute, DateTime].dateTime)
      checkAll("NodeDateTime[ConstNode, Hour]", NodeDateTimeTests[ConstNode, Hour, DateTime].dateTime)
      checkAll("NodeDateTime[ConstNode, DayOfMonth]", NodeDateTimeTests[ConstNode, DayOfMonth, DateTime].dateTime)
      checkAll("NodeDateTime[ConstNode, Month]", NodeDateTimeTests[ConstNode, Month, DateTime].dateTime)
      checkAll("NodeDateTime[ConstNode, DayOfWeek]", NodeDateTimeTests[ConstNode, DayOfWeek, DateTime].dateTime)
    }
  }

  object between extends NodeCheck with ArbitraryBetweenNode {
    def check() = {
      checkAll("NodeDateTime[BetweenNode, Second]", NodeDateTimeTests[BetweenNode, Second, DateTime].dateTime)
      checkAll("NodeDateTime[BetweenNode, Minute]", NodeDateTimeTests[BetweenNode, Minute, DateTime].dateTime)
      checkAll("NodeDateTime[BetweenNode, Hour]", NodeDateTimeTests[BetweenNode, Hour, DateTime].dateTime)
      checkAll("NodeDateTime[BetweenNode, DayOfMonth]", NodeDateTimeTests[BetweenNode, DayOfMonth, DateTime].dateTime)
      checkAll("NodeDateTime[BetweenNode, Month]", NodeDateTimeTests[BetweenNode, Month, DateTime].dateTime)
      checkAll("NodeDateTime[BetweenNode, DayOfWeek]", NodeDateTimeTests[BetweenNode, DayOfWeek, DateTime].dateTime)
    }
  }

  object several extends NodeCheck with ArbitrarySeveralNode {
    def check() = {
      checkAll("NodeDateTime[SeveralNode, Second]", NodeDateTimeTests[SeveralNode, Second, DateTime].dateTime)
      checkAll("NodeDateTime[SeveralNode, Minute]", NodeDateTimeTests[SeveralNode, Minute, DateTime].dateTime)
      checkAll("NodeDateTime[SeveralNode, Hour]", NodeDateTimeTests[SeveralNode, Hour, DateTime].dateTime)
      checkAll("NodeDateTime[SeveralNode, DayOfMonth]", NodeDateTimeTests[SeveralNode, DayOfMonth, DateTime].dateTime)
      checkAll("NodeDateTime[SeveralNode, Month]", NodeDateTimeTests[SeveralNode, Month, DateTime].dateTime)
      checkAll("NodeDateTime[SeveralNode, DayOfWeek]", NodeDateTimeTests[SeveralNode, DayOfWeek, DateTime].dateTime)
    }
  }

  object every extends NodeCheck with ArbitraryEveryNode {
    def check() = {
      checkAll("NodeDateTime[EveryNode, Second]", NodeDateTimeTests[EveryNode, Second, DateTime].dateTime)
      checkAll("NodeDateTime[EveryNode, Minute]", NodeDateTimeTests[EveryNode, Minute, DateTime].dateTime)
      checkAll("NodeDateTime[EveryNode, Hour]", NodeDateTimeTests[EveryNode, Hour, DateTime].dateTime)
      checkAll("NodeDateTime[EveryNode, DayOfMonth]", NodeDateTimeTests[EveryNode, DayOfMonth, DateTime].dateTime)
      checkAll("NodeDateTime[EveryNode, Month]", NodeDateTimeTests[EveryNode, Month, DateTime].dateTime)
      checkAll("NodeDateTime[EveryNode, DayOfWeek]", NodeDateTimeTests[EveryNode, DayOfWeek, DateTime].dateTime)
    }
  }

  for (node <- Seq(each, const, between, several, every)) node.check()

}
