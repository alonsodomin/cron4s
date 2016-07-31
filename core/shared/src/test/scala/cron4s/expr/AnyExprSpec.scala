package cron4s.expr

import cron4s.BaseGenerators
import org.scalacheck._

/**
  * Created by alonsodomin on 31/07/2016.
  */
object AnyExprSpec extends Properties("AnyExpr") with BaseGenerators {
  import Prop._
  import Expr.AnyExpr

  property("min should be the same as its unit") = forAll(cronUnitGen) { unit =>
    AnyExpr()(unit).min == unit.min
  }

  property("max should be the same as its unit") = forAll(cronUnitGen) { unit =>
    AnyExpr()(unit).max == unit.max
  }

  property("match should always succeed inside units range") = forAll(cronUnitAndValueGen) {
    case (unit, value) => AnyExpr()(unit).matches(value)
  }

  property("match should always fail outside its units range") = forAll(cronUnitAndValueOutsideRangeGen) {
    case (unit, value) => !AnyExpr()(unit).matches(value)
  }

  val stepsGen = for {
    unit  <- cronUnitGen
    value <- Gen.posNum[Int]
    step  <- Gen.choose(-100, 100)
  } yield (unit, value, step)

  property("step should be the same as its unit") = forAll(stepsGen) {
    case (unit, value, step) =>
      AnyExpr()(unit).step(value, step) == unit.step(value, step)
  }

}
