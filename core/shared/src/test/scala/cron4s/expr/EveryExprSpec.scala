package cron4s.expr

import cron4s.types.Sequential

import org.scalacheck._

/**
  * Created by alonsodomin on 01/08/2016.
  */
object EveryExprSpec extends Properties("EveryExpr") with ExprGenerators {
  import Prop._
  import Arbitrary.arbitrary

  def generateRange(expr: EveryExpr[_]): IndexedSeq[Int] = {
    Stream.iterate[Option[(Int, Int)]](Some(expr.min -> 0)) {
      prev => prev.flatMap { case (v, _) => expr.value.step(v, expr.freq) }
    }.flatten.takeWhile(_._2 < 1).map(_._1).toVector
  }

  property("min should be equal to its base min") = forAll(everyExpressions) {
    expr => expr.min == expr.value.min
  }

  property("max should be equal to its base max") = forAll(everyExpressions) {
    expr => expr.max == expr.value.max
  }

  property("range must be stepped progression over its base") = forAll(everyExpressions) {
    expr =>
      val elems = generateRange(expr)
      expr.range == elems
  }

  val valuesWithinRange = for {
    expr  <- everyExpressions
    value <- Gen.oneOf(expr.range)
  } yield (expr, value)

  property("should match any value within its own range") = forAll(valuesWithinRange) {
    case (expr, value) => expr.matches(value)
  }

  val valuesOutsideRange = for {
    expr  <- everyExpressions
    value <- arbitrary[Int] if value < expr.min || value > expr.max
  } yield expr -> value

  property("should not match values outside the range") = forAll(valuesOutsideRange) {
    case (expr, value) => !expr.matches(value)
  }

  val stepsFromOutsideRange = for {
    expr      <- everyExpressions
    fromValue <- arbitrary[Int] if fromValue < expr.unit.min || fromValue > expr.unit.max
    stepSize  <- arbitrary[Int]
  } yield (expr, fromValue, stepSize)

  property("stepping from outside the range should yield None") = forAll(stepsFromOutsideRange) {
    case (expr, fromValue, stepSize) =>
      expr.step(fromValue, stepSize).isEmpty
  }

  val stepsFromInsideRange = for {
    expr      <- everyExpressions
    fromValue <- Gen.choose(expr.min, expr.max)
    stepSize  <- arbitrary[Int]
  } yield (expr, fromValue, stepSize)

  property("stepping with a zero step size does nothing") = forAll(stepsFromInsideRange) {
    case (expr, fromValue, stepSize) => expr.step(fromValue, 0).contains(fromValue -> 0)
  }

  property("stepping with a non-zero size is the same as stepping inside the internal expression multiplied by the frequency") = forAll(stepsFromInsideRange) {
    case (expr, fromValue, stepSize) =>
      val internalRange = Sequential.sequential(expr.range)
      expr.step(fromValue, stepSize) == internalRange.step(fromValue, stepSize * expr.freq)
  }

}
