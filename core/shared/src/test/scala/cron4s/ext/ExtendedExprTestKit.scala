package cron4s.ext

import cron4s._
import cron4s.expr._

import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

import scalaz.Equal

/**
  * Created by alonsodomin on 04/08/2016.
  */
abstract class ExtendedExprTestKit[DateTime : DateTimeAdapter : Equal : Arbitrary] extends FunSuite with Discipline {
  import CronField._

  trait ExprCheck {
    def check(): Unit
  }

  object any extends ExprCheck with ArbitraryAnyExpr {
    def check() = {
      checkAll("ExtendedExpr[AnyExpr, Minute]", ExtendedExprTests[AnyExpr, Minute.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[AnyExpr, Hour]", ExtendedExprTests[AnyExpr, Hour.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[AnyExpr, DayOfMonth]", ExtendedExprTests[AnyExpr, DayOfMonth.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[AnyExpr, Month]", ExtendedExprTests[AnyExpr, Month.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[AnyExpr, DayOfWeek]", ExtendedExprTests[AnyExpr, DayOfWeek.type, DateTime].extendedExpr)
    }
  }

  object const extends ExprCheck with ArbitraryConstExpr {
    def check() = {
      checkAll("ExtendedExpr[ConstExpr, Minute]", ExtendedExprTests[ConstExpr, Minute.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, Hour]", ExtendedExprTests[ConstExpr, Hour.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, DayOfMonth]", ExtendedExprTests[ConstExpr, DayOfMonth.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, Month]", ExtendedExprTests[ConstExpr, Month.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[ConstExpr, DayOfWeek]", ExtendedExprTests[ConstExpr, DayOfWeek.type, DateTime].extendedExpr)
    }
  }

  object between extends ExprCheck with ArbitraryBetweenExpr {
    def check() = {
      checkAll("ExtendedExpr[BetweenExpr, Minute]", ExtendedExprTests[BetweenExpr, Minute.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, Hour]", ExtendedExprTests[BetweenExpr, Hour.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, DayOfMonth]", ExtendedExprTests[BetweenExpr, DayOfMonth.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, Month]", ExtendedExprTests[BetweenExpr, Month.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[BetweenExpr, DayOfWeek]", ExtendedExprTests[BetweenExpr, DayOfWeek.type, DateTime].extendedExpr)
    }
  }

  object several extends ExprCheck with ArbitrarySeveralExpr {
    def check() = {
      checkAll("ExtendedExpr[SeveralExpr, Minute]", ExtendedExprTests[SeveralExpr, Minute.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, Hour]", ExtendedExprTests[SeveralExpr, Hour.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, DayOfMonth]", ExtendedExprTests[SeveralExpr, DayOfMonth.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, Month]", ExtendedExprTests[SeveralExpr, Month.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[SeveralExpr, DayOfWeek]", ExtendedExprTests[SeveralExpr, DayOfWeek.type, DateTime].extendedExpr)
    }
  }

  object every extends ExprCheck with ArbitraryEveryExpr {
    def check() = {
      checkAll("ExtendedExpr[EveryExpr, Minute]", ExtendedExprTests[EveryExpr, Minute.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, Hour]", ExtendedExprTests[EveryExpr, Hour.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, DayOfMonth]", ExtendedExprTests[EveryExpr, DayOfMonth.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, Month]", ExtendedExprTests[EveryExpr, Month.type, DateTime].extendedExpr)
      checkAll("ExtendedExpr[EveryExpr, DayOfWeek]", ExtendedExprTests[EveryExpr, DayOfWeek.type, DateTime].extendedExpr)
    }
  }

  for (exprType <- Seq(any, const, between, several, every)) exprType.check()

}
