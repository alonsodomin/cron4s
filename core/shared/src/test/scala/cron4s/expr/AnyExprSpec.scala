package cron4s.expr

import cron4s.BaseGenerators
import org.scalacheck._

/**
  * Created by alonsodomin on 31/07/2016.
  */
object AnyExprSpec extends Properties("AnyExpr") with BaseGenerators {
  import Prop._
  import Expr.AnyExpr
  import Arbitrary.arbitrary

  property("min should be the same as its unit") = forAll(cronUnits) { unit =>
    AnyExpr()(unit).min == unit.min
  }

  property("max should be the same as its unit") = forAll(cronUnits) { unit =>
    AnyExpr()(unit).max == unit.max
  }

  property("match should always succeed inside units range") = forAll(cronUnitAndValues) {
    case (unit, value) => AnyExpr()(unit).matches(value)
  }

  property("match should always fail outside its units range") = forAll(cronUnitAndValuesOutsideRange) {
    case (unit, value) => !AnyExpr()(unit).matches(value)
  }

  val valuesAndSteps = for {
    unit  <- cronUnits
    value <- arbitrary[Int]
    step  <- arbitrary[Int]
  } yield (unit, value, step)

  property("step should be the same as its unit") = forAll(valuesAndSteps) {
    case (unit, value, step) =>
      AnyExpr()(unit).step(value, step) == unit.step(value, step)
  }

}
