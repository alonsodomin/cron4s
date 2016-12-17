package cron4s.testkit

import cron4s._
import cron4s.expr._
import cron4s.spi.DateTimeAdapter
import cron4s.testkit.discipline.ExtendedExprTests
import cron4s.testkit.gen._

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite

import org.typelevel.discipline.scalatest.Discipline

import scalaz.Equal

/**
  * Created by alonsodomin on 04/08/2016.
  */
abstract class ExtendedExprTestKit[DateTime <: AnyRef : DateTimeAdapter : Equal]
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
      checkAll("ExtendedExpr[EachExpr, Second]", ExtendedExprTests[EachExpr, Second, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EachExpr, Minute]", ExtendedExprTests[EachExpr, Minute, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EachExpr, Hour]", ExtendedExprTests[EachExpr, Hour, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EachExpr, DayOfMonth]", ExtendedExprTests[EachExpr, DayOfMonth, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EachExpr, Month]", ExtendedExprTests[EachExpr, Month, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EachExpr, DayOfWeek]", ExtendedExprTests[EachExpr, DayOfWeek, DateTime].extendedExpr)
    }
  }

  object const extends ExprCheck with ArbitraryConstExpr {
    def check() = {
      checkAll("ExtendedExpr[ConstExpr, Second]", ExtendedExprTests[ConstExpr, Second, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, Minute]", ExtendedExprTests[ConstExpr, Minute, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, Hour]", ExtendedExprTests[ConstExpr, Hour, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, DayOfMonth]", ExtendedExprTests[ConstExpr, DayOfMonth, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, Month]", ExtendedExprTests[ConstExpr, Month, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, DayOfWeek]", ExtendedExprTests[ConstExpr, DayOfWeek, DateTime].extendedExpr)
    }
  }

  object between extends ExprCheck with ArbitraryBetweenExpr {
    def check() = {
      checkAll("ExtendedExpr[BetweenExpr, Second]", ExtendedExprTests[BetweenExpr, Second, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, Minute]", ExtendedExprTests[BetweenExpr, Minute, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, Hour]", ExtendedExprTests[BetweenExpr, Hour, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, DayOfMonth]", ExtendedExprTests[BetweenExpr, DayOfMonth, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, Month]", ExtendedExprTests[BetweenExpr, Month, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, DayOfWeek]", ExtendedExprTests[BetweenExpr, DayOfWeek, DateTime].extendedExpr)
    }
  }

  object several extends ExprCheck with ArbitrarySeveralExpr {
    def check() = {
      checkAll("ExtendedExpr[SeveralExpr, Second]", ExtendedExprTests[SeveralExpr, Second, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, Minute]", ExtendedExprTests[SeveralExpr, Minute, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, Hour]", ExtendedExprTests[SeveralExpr, Hour, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, DayOfMonth]", ExtendedExprTests[SeveralExpr, DayOfMonth, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, Month]", ExtendedExprTests[SeveralExpr, Month, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, DayOfWeek]", ExtendedExprTests[SeveralExpr, DayOfWeek, DateTime].extendedExpr)
    }
  }

  object every extends ExprCheck with ArbitraryEveryExpr {
    def check() = {
      checkAll("ExtendedExpr[EveryExpr, Second]", ExtendedExprTests[EveryExpr, Second, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, Minute]", ExtendedExprTests[EveryExpr, Minute, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, Hour]", ExtendedExprTests[EveryExpr, Hour, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, DayOfMonth]", ExtendedExprTests[EveryExpr, DayOfMonth, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, Month]", ExtendedExprTests[EveryExpr, Month, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, DayOfWeek]", ExtendedExprTests[EveryExpr, DayOfWeek, DateTime].extendedExpr)
    }
  }

  for (exprType <- Seq(any, const, between, several, every)) exprType.check()

}
