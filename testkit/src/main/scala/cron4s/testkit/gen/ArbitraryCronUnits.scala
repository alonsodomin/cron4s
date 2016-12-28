package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit

import org.scalacheck._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryCronUnits {

  implicit lazy val arbitrarySecondsUnit     = Arbitrary(Gen.const(CronUnit[Second]))
  implicit lazy val arbitraryMinutesUnit     = Arbitrary(Gen.const(CronUnit[Minute]))
  implicit lazy val arbitraryHoursUnit       = Arbitrary(Gen.const(CronUnit[Hour]))
  implicit lazy val arbitraryDaysOfMonthUnit = Arbitrary(Gen.const(CronUnit[DayOfMonth]))
  implicit lazy val arbitraryMonthsUnit      = Arbitrary(Gen.const(CronUnit[Month]))
  implicit lazy val arbitraryDaysOfWeekUnit  = Arbitrary(Gen.const(CronUnit[DayOfWeek]))

}
