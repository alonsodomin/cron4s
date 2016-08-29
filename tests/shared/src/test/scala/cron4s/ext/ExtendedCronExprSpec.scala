package cron4s.ext

import cron4s._
import cron4s.expr.{AnyExpr, CronExpr}
import org.scalacheck._
import shapeless._

/**
  * Created by alonsodomin on 06/08/2016.
  */
class ExtendedCronExprSpec extends Properties("ExtendedCronExpr") {
  import Arbitrary.arbitrary
  import CronField._
  import Prop._
  import testdummy._

  val anyExpr = CronExpr(AnyExpr[Second.type] :: AnyExpr[Minute.type] :: AnyExpr[Hour.type] :: AnyExpr[DayOfMonth.type] ::
    AnyExpr[Month.type] :: AnyExpr[DayOfWeek.type] :: HNil)

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
    case (expr, dt) => expr.previous(dt) == expr.step(dt, -1)
  }

}
