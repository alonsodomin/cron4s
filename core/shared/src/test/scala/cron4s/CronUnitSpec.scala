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

  val stepsFromWithinRange = for {
    unit      <- cronUnits
    fromValue <- Gen.choose(unit.min, unit.max)
    stepSize  <- arbitrary[Int]
  } yield (unit, fromValue, stepSize)

  property("stepping from inside the range yields sequential values") = forAll(stepsFromWithinRange) {
    case (unit, fromValue, stepSize) =>
      val seq = Sequential.sequential(unit.range)
      unit.step(fromValue, stepSize) == seq.step(fromValue, stepSize)
  }

}
