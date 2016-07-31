package cron4s

import org.scalacheck._

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait BaseGenerators {
  import Arbitrary.arbitrary

  lazy val cronFields: Gen[_ <: CronField] = Gen.oneOf(
    CronField.Minute,
    CronField.Hour,
    CronField.DayOfMonth,
    CronField.Month,
    CronField.DayOfWeek
  )
  implicit lazy val arbitraryCronField = Arbitrary(cronFields)

  lazy val cronUnits: Gen[CronUnit[_ <: CronField]] = Gen.oneOf(
    CronUnit.Minutes,
    CronUnit.Hours,
    CronUnit.DaysOfMonth,
    CronUnit.Months,
    CronUnit.DaysOfWeek
  )
  implicit lazy val arbitraryCronUnit = Arbitrary(cronUnits)

  lazy val cronUnitAndValues: Gen[(CronUnit[_ <: CronField], Int)] = for {
    unit  <- cronUnits
    value <- Gen.oneOf(unit.values)
  } yield (unit, value)

  lazy val cronUnitAndValuesOutsideRange: Gen[(CronUnit[_ <: CronField], Int)] = for {
    unit  <- cronUnits
    value <- arbitrary[Int] if !unit.values.contains(value)
  } yield (unit, value)

}
