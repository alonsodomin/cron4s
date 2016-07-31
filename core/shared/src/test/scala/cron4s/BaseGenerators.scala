package cron4s

import cron4s.expr.AnyExprSpec._
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait BaseGenerators {

  def cronFieldGen: Gen[CronField] = Gen.oneOf(List(
    CronField.Minute, CronField.Hour, CronField.DayOfMonth, CronField.Month, CronField.DayOfWeek
  ))
  implicit def arbitraryCronField = Arbitrary(cronFieldGen)

  def cronUnitGen: Gen[CronUnit[_ <: CronField]] = Gen.oneOf(List(
    CronUnit.MinutesUnit,
    CronUnit.HoursUnit,
    CronUnit.DaysOfMonthUnit,
    CronUnit.MonthsUnit,
    CronUnit.DaysOfWeekUnit
  ))
  implicit def arbitraryCronUnit = Arbitrary(cronUnitGen)

  def cronUnitAndValueGen: Gen[(CronUnit[_ <: CronField], Int)] = for {
    unit  <- cronUnitGen
    value <- Gen.oneOf(unit.values)
  } yield (unit, value)

  def cronUnitAndValueOutsideRangeGen: Gen[(CronUnit[_ <: CronField], Int)] = for {
    unit  <- cronUnitGen
    value <- implicitly[Arbitrary[Int]].arbitrary if !unit.values.contains(value)
  } yield (unit, value)

}
