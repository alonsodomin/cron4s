package cron4s

import cron4s.testkit.discipline.HasCronFieldTests

import org.scalatest.{FunSuite, Matchers}
import org.typelevel.discipline.scalatest.Discipline

class CronUnitSpec extends FunSuite with Discipline with ArbitraryCronUnits {
  import CronField._

  checkAll("CronUnit[Minute]", HasCronFieldTests[CronUnit, Minute.type].hasCronField)
  checkAll("CronUnit[Hour]", HasCronFieldTests[CronUnit, Hour.type].hasCronField)
  checkAll("CronUnit[DayOfMonth]", HasCronFieldTests[CronUnit, DayOfMonth.type].hasCronField)
  checkAll("CronUnit[Month]", HasCronFieldTests[CronUnit, Month.type].hasCronField)
  checkAll("CronUnit[DayOfWeek]", HasCronFieldTests[CronUnit, DayOfWeek.type].hasCronField)

  /*import Prop._
  import Arbitrary.arbitrary

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
      unit.step(fromValue, stepSize) == seq.step(fromValue, stepSize)
  }*/

}
