package cron4s.expr

import cron4s.matcher
import cron4s.types.std.all._

import org.scalacheck._

/**
  * Created by alonsodomin on 01/08/2016.
  */
object SeveralExprSpec extends Properties("SeveralExpr") with ExprGenerators {
  import Prop._
  import Arbitrary.arbitrary

  property("min should be the min value of the head") = forAll(severalExpressions) {
    expr => expr.min == expr.values.head.min
  }

  property("max should be the max value of the last") = forAll(severalExpressions) {
    expr => expr.max == expr.values.last.max
  }

  val valuesOutsideRange = for {
    expr <- severalExpressions
    value <- arbitrary[Int] if !matcher.exists(expr.values.map(_.matches)).apply(value)
  } yield (expr, value)

  val valuesInsideRange = for {
    expr <- severalExpressions
    value <- Gen.choose(expr.min, expr.max) if matcher.exists(expr.values.map(_.matches)).apply(value)
  } yield (expr, value)

  property("should not match values outside the range of its elements") = forAll(valuesOutsideRange) {
    case (expr, value) => !expr.matches(value)
  }

  property("should match values inside the range of its elements") = forAll(valuesInsideRange) {
    case (expr, value) => expr.matches(value)
  }

  val stepsFromOutsideRange = for {
    expr      <- severalExpressions
    fromValue <- arbitrary[Int] if !expr.matches(fromValue)
    stepSize  <- arbitrary[Int]
  } yield (expr, fromValue, stepSize)

  property("stepping from outside the range returns none") = forAll(stepsFromOutsideRange) {
    case (expr, fromValue, stepSize) =>
      expr.step(fromValue, stepSize).isEmpty
  }

  val stepsFromInsideRange = for {
    expr      <- severalExpressions
    fromValue <- Gen.choose(expr.min, expr.max) if expr.matches(fromValue)
    stepSize  <- arbitrary[Int]
  } yield (expr, fromValue, stepSize)

  property("stepping with a zero size step does nothing") = forAll(stepsFromInsideRange) {
    case (expr, fromValue, _) => expr.step(fromValue, 0).contains((fromValue, 0))
  }

  property("stepping with a non-zero size is the same as stepping inside the internal expression") = forAll(stepsFromInsideRange) {
    case (expr, fromValue, stepSize) =>
      val internalExpr = expr.values.dropWhile(!_.matches(fromValue)).head
      expr.step(fromValue, stepSize) == internalExpr.step(fromValue, stepSize)
  }

}
