package cron4s

import cron4s.types.Sequential

import org.scalacheck._

object CronUnitSpec extends Properties("CronUnit") with BaseGenerators {
  import Prop._
  import Arbitrary.arbitrary

  val unitsAndValues = for {
    unit  <- cronUnits
    value <- Gen.choose(unit.min, unit.max)
  } yield unit -> value

  property("indexOf should be the same as looking in the range") = forAll(unitsAndValues) {
    case (unit, value) => unit.indexOf(value).get == unit.range.indexOf(value)
  }

  val stepsFromOutsideRange = for {
    unit      <- cronUnits
    fromValue <- arbitrary[Int] if fromValue < unit.min || fromValue > unit.max
    stepSize  <- arbitrary[Int]
  } yield (unit, fromValue, stepSize)

  property("stepping from outside the range yields None") = forAll(stepsFromOutsideRange) {
    case (unit, fromValue, stepSize) => unit.step(fromValue, stepSize).isEmpty
  }

  val stepsFromWithinRange = for {
    unit      <- cronUnits
    fromValue <- Gen.choose(unit.min, unit.max)
    stepSize  <- arbitrary[Int]
  } yield (unit, fromValue, stepSize)

  property("stepping with a zero step size returns the input") = forAll(stepsFromWithinRange) {
    case (unit, fromValue, _) => unit.step(fromValue, 0).contains(fromValue -> 0)
  }

  property("stepping from inside the range yields sequential values") = forAll(stepsFromWithinRange) {
    case (unit, fromValue, stepSize) =>
      val seq = Sequential.sequential(unit.range)
      unit.step(fromValue, stepSize) == seq.step(fromValue, stepSize)
  }

}
