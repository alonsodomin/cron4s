package cron4s.ext

import cron4s._
import cron4s.expr.{AnyExpr, CronExpr}

import org.scalacheck._

import shapeless._

/**
  * Created by alonsodomin on 06/08/2016.
  */
class ExtendedCronExprSpec extends Properties("ExtendedCronExpr") with DummyTestBase {
  import Arbitrary.arbitrary
  import CronField._
  import CronUnit._
  import Prop._
  import testdummy._

  implicit lazy val arbitraryDateTime = Arbitrary(for {
    seconds     <- Gen.choose(Seconds.min, Seconds.max)
    minutes     <- Gen.choose(Minutes.min, Minutes.max)
    hours       <- Gen.choose(Hours.min, Hours.max)
    daysOfMonth <- Gen.choose(DaysOfMonth.min, DaysOfMonth.max)
    months      <- Gen.choose(Months.min, Months.max)
    daysOfWeek  <- Gen.choose(DaysOfWeek.min, DaysOfWeek.max)
  } yield createDateTime(seconds, minutes, hours, daysOfMonth, months, daysOfWeek))

  val anyExpr = CronExpr(
    AnyExpr[Second],
    AnyExpr[Minute],
    AnyExpr[Hour],
    AnyExpr[DayOfMonth],
    AnyExpr[Month],
    AnyExpr[DayOfWeek]
  )

  val anyDateCombinations = for {
    expr <- Gen.const(anyExpr)
    dt   <- arbitrary[DummyDateTime]
  } yield (expr, dt)

  property("any expression matches everything") = forAll(anyDateCombinations) {
    case (expr, dt) => expr.allOf(dt) && expr.anyOf(dt)
  }

  property("next is equals to step with 1") = forAll(anyDateCombinations) {
    case (expr, dt) => expr.next(dt) == expr.step(dt, 1)
  }

  property("previous is equals to step with -1") = forAll(anyDateCombinations) {
    case (expr, dt) => expr.prev(dt) == expr.step(dt, -1)
  }

}
