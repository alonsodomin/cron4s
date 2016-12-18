package cron4s.testkit

import cron4s._
import cron4s.expr._
import cron4s.spi.DateTimeAdapter
import cron4s.testkit.discipline.ExprDateTimeTests
import cron4s.testkit.gen._

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite

import org.typelevel.discipline.scalatest.Discipline

import scalaz.Equal

/**
  * Created by alonsodomin on 04/08/2016.
  */
abstract class ExprDateTimeTestKit[DateTime <: AnyRef : DateTimeAdapter : Equal]
  extends FunSuite with Discipline with ExtensionsTestKitBase[DateTime] {
  import CronField._
  import CronUnit._

  trait ExprCheck {
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

  object any extends ExprCheck with ArbitraryEachExpr {
    def check() = {
      checkAll("ExprDateTime[EachExpr, Second]", ExprDateTimeTests[EachExpr, Second, DateTime].extendedExpr)
      checkAll("ExprDateTime[EachExpr, Minute]", ExprDateTimeTests[EachExpr, Minute, DateTime].extendedExpr)
      checkAll("ExprDateTime[EachExpr, Hour]", ExprDateTimeTests[EachExpr, Hour, DateTime].extendedExpr)
      checkAll("ExprDateTime[EachExpr, DayOfMonth]", ExprDateTimeTests[EachExpr, DayOfMonth, DateTime].extendedExpr)
      checkAll("ExprDateTime[EachExpr, Month]", ExprDateTimeTests[EachExpr, Month, DateTime].extendedExpr)
      checkAll("ExprDateTime[EachExpr, DayOfWeek]", ExprDateTimeTests[EachExpr, DayOfWeek, DateTime].extendedExpr)
    }
  }

  object const extends ExprCheck with ArbitraryConstExpr {
    def check() = {
      checkAll("ExprDateTime[ConstExpr, Second]", ExprDateTimeTests[ConstExpr, Second, DateTime].extendedExpr)
      checkAll("ExprDateTime[ConstExpr, Minute]", ExprDateTimeTests[ConstExpr, Minute, DateTime].extendedExpr)
      checkAll("ExprDateTime[ConstExpr, Hour]", ExprDateTimeTests[ConstExpr, Hour, DateTime].extendedExpr)
      checkAll("ExprDateTime[ConstExpr, DayOfMonth]", ExprDateTimeTests[ConstExpr, DayOfMonth, DateTime].extendedExpr)
      checkAll("ExprDateTime[ConstExpr, Month]", ExprDateTimeTests[ConstExpr, Month, DateTime].extendedExpr)
      checkAll("ExprDateTime[ConstExpr, DayOfWeek]", ExprDateTimeTests[ConstExpr, DayOfWeek, DateTime].extendedExpr)
    }
  }

  object between extends ExprCheck with ArbitraryBetweenExpr {
    def check() = {
      checkAll("ExprDateTime[BetweenExpr, Second]", ExprDateTimeTests[BetweenExpr, Second, DateTime].extendedExpr)
      checkAll("ExprDateTime[BetweenExpr, Minute]", ExprDateTimeTests[BetweenExpr, Minute, DateTime].extendedExpr)
      checkAll("ExprDateTime[BetweenExpr, Hour]", ExprDateTimeTests[BetweenExpr, Hour, DateTime].extendedExpr)
      checkAll("ExprDateTime[BetweenExpr, DayOfMonth]", ExprDateTimeTests[BetweenExpr, DayOfMonth, DateTime].extendedExpr)
      checkAll("ExprDateTime[BetweenExpr, Month]", ExprDateTimeTests[BetweenExpr, Month, DateTime].extendedExpr)
      checkAll("ExprDateTime[BetweenExpr, DayOfWeek]", ExprDateTimeTests[BetweenExpr, DayOfWeek, DateTime].extendedExpr)
    }
  }

  object several extends ExprCheck with ArbitrarySeveralExpr {
    def check() = {
      checkAll("ExprDateTime[SeveralExpr, Second]", ExprDateTimeTests[SeveralExpr, Second, DateTime].extendedExpr)
      checkAll("ExprDateTime[SeveralExpr, Minute]", ExprDateTimeTests[SeveralExpr, Minute, DateTime].extendedExpr)
      checkAll("ExprDateTime[SeveralExpr, Hour]", ExprDateTimeTests[SeveralExpr, Hour, DateTime].extendedExpr)
      checkAll("ExprDateTime[SeveralExpr, DayOfMonth]", ExprDateTimeTests[SeveralExpr, DayOfMonth, DateTime].extendedExpr)
      checkAll("ExprDateTime[SeveralExpr, Month]", ExprDateTimeTests[SeveralExpr, Month, DateTime].extendedExpr)
      checkAll("ExprDateTime[SeveralExpr, DayOfWeek]", ExprDateTimeTests[SeveralExpr, DayOfWeek, DateTime].extendedExpr)
    }
  }

  object every extends ExprCheck with ArbitraryEveryExpr {
    def check() = {
      checkAll("ExprDateTime[EveryExpr, Second]", ExprDateTimeTests[EveryExpr, Second, DateTime].extendedExpr)
      checkAll("ExprDateTime[EveryExpr, Minute]", ExprDateTimeTests[EveryExpr, Minute, DateTime].extendedExpr)
      checkAll("ExprDateTime[EveryExpr, Hour]", ExprDateTimeTests[EveryExpr, Hour, DateTime].extendedExpr)
      checkAll("ExprDateTime[EveryExpr, DayOfMonth]", ExprDateTimeTests[EveryExpr, DayOfMonth, DateTime].extendedExpr)
      checkAll("ExprDateTime[EveryExpr, Month]", ExprDateTimeTests[EveryExpr, Month, DateTime].extendedExpr)
      checkAll("ExprDateTime[EveryExpr, DayOfWeek]", ExprDateTimeTests[EveryExpr, DayOfWeek, DateTime].extendedExpr)
    }
  }

  for (exprType <- Seq(any, const, between, several, every)) exprType.check()

}
