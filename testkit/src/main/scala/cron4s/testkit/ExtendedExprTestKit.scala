package cron4s.testkit

import cron4s._
import cron4s.expr._
import cron4s.ext.DateTimeAdapter
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

  object any extends ExprCheck with ArbitraryAnyExpr {
    def check() = {
      checkAll("ExtendedExpr[AnyExpr, Second]", ExtendedExprTests[AnyExpr, Second.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[AnyExpr, Minute]", ExtendedExprTests[AnyExpr, Minute.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[AnyExpr, Hour]", ExtendedExprTests[AnyExpr, Hour.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[AnyExpr, DayOfMonth]", ExtendedExprTests[AnyExpr, DayOfMonth.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[AnyExpr, Month]", ExtendedExprTests[AnyExpr, Month.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[AnyExpr, DayOfWeek]", ExtendedExprTests[AnyExpr, DayOfWeek.type, DateTime].extendedExpr)
    }
  }

  object const extends ExprCheck with ArbitraryConstExpr {
    def check() = {
      checkAll("ExtendedExpr[ConstExpr, Second]", ExtendedExprTests[ConstExpr, Second.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, Minute]", ExtendedExprTests[ConstExpr, Minute.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, Hour]", ExtendedExprTests[ConstExpr, Hour.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, DayOfMonth]", ExtendedExprTests[ConstExpr, DayOfMonth.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, Month]", ExtendedExprTests[ConstExpr, Month.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, DayOfWeek]", ExtendedExprTests[ConstExpr, DayOfWeek.type, DateTime].extendedExpr)
    }
  }

  object between extends ExprCheck with ArbitraryBetweenExpr {
    def check() = {
      checkAll("ExtendedExpr[BetweenExpr, Second]", ExtendedExprTests[BetweenExpr, Second.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, Minute]", ExtendedExprTests[BetweenExpr, Minute.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, Hour]", ExtendedExprTests[BetweenExpr, Hour.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, DayOfMonth]", ExtendedExprTests[BetweenExpr, DayOfMonth.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, Month]", ExtendedExprTests[BetweenExpr, Month.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, DayOfWeek]", ExtendedExprTests[BetweenExpr, DayOfWeek.type, DateTime].extendedExpr)
    }
  }

  object several extends ExprCheck with ArbitrarySeveralExpr {
    def check() = {
      checkAll("ExtendedExpr[SeveralExpr, Second]", ExtendedExprTests[SeveralExpr, Second.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, Minute]", ExtendedExprTests[SeveralExpr, Minute.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, Hour]", ExtendedExprTests[SeveralExpr, Hour.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, DayOfMonth]", ExtendedExprTests[SeveralExpr, DayOfMonth.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, Month]", ExtendedExprTests[SeveralExpr, Month.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, DayOfWeek]", ExtendedExprTests[SeveralExpr, DayOfWeek.type, DateTime].extendedExpr)
    }
  }

  object every extends ExprCheck with ArbitraryEveryExpr {
    def check() = {
      checkAll("ExtendedExpr[EveryExpr, Second]", ExtendedExprTests[EveryExpr, Second.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, Minute]", ExtendedExprTests[EveryExpr, Minute.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, Hour]", ExtendedExprTests[EveryExpr, Hour.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, DayOfMonth]", ExtendedExprTests[EveryExpr, DayOfMonth.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, Month]", ExtendedExprTests[EveryExpr, Month.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, DayOfWeek]", ExtendedExprTests[EveryExpr, DayOfWeek.type, DateTime].extendedExpr)
    }
  }

  for (exprType <- Seq(any, const, between, several, every)) exprType.check()

}
