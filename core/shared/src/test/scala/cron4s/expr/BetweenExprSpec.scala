package cron4s.expr

import cron4s.BaseFunctions
import org.scalacheck._

/**
  * Created by alonsodomin on 31/07/2016.
  */
object BetweenExprSpec extends Properties("BetweenExpr") with ExprGenerators with BaseFunctions {
  import Prop._
  import Arbitrary.arbitrary

  property("min should be the begin constant") = forAll(betweenExpressions) {
    expr => expr.begin.value == expr.min
  }

  property("max should be the end constant") = forAll(betweenExpressions) {
    expr => expr.end.value == expr.max
  }

  val valuesOutsideRange = for {
    expr  <- betweenExpressions
    value <- arbitrary[Int] if value < expr.min || value > expr.max
  } yield (expr, value)

  val valuesInsideRange = for {
    expr  <- betweenExpressions
    value <- Gen.choose(expr.min, expr.max)
  } yield (expr, value)

  property("should not match values outside its own range") = forAll(valuesOutsideRange) {
    case (expr, value) => !expr.matches(value)
  }

  property("should match values inside its own range") = forAll(valuesInsideRange) {
    case (expr, value) => expr.matches(value)
  }

  val stepsFromOutsideRange = for {
    expr      <- betweenExpressions
    fromValue <- arbitrary[Int] if fromValue < expr.min || fromValue > expr.max
    stepSize  <- arbitrary[Int]
  } yield (expr, fromValue, stepSize)

  property("stepping from outside the range returns none") = forAll(stepsFromOutsideRange) {
    case (expr, fromValue, stepSize) => expr.step(fromValue, stepSize).isEmpty
  }

  val stepsFromInsideRange = for {
    expr      <- betweenExpressions
    fromValue <- Gen.choose(expr.min, expr.max)
    stepSize  <- arbitrary[Int]
  } yield (expr, fromValue, stepSize)

  property("stepping with a zero step size returns the from value") = forAll(stepsFromInsideRange) {
    case (expr, fromValue, _) => expr.step(fromValue, 0).contains((fromValue, 0))
  }

  val stepsIntoInsideRange = for {
    expr      <- betweenExpressions
    fromValue <- Gen.choose(expr.min, expr.max)
    stepSize  <- arbitrary[Int]
  } yield (expr, fromValue, stepSize)

  property("stepping from inside the range is the same as narrowing the unit") = forAll(stepsIntoInsideRange) {
    case (expr, fromValue, stepSize) =>
      val unitStep = expr.unit.narrow(expr.min, expr.max).step(fromValue, stepSize)
      expr.step(fromValue, stepSize) == unitStep
  }

}
