package cron4s

import cron4s.CronField._
import org.scalacheck._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryCronUnits {

  implicit lazy val arbitraryMinutesUnit     = Arbitrary(Gen.const(CronUnit[Minute.type]))
  implicit lazy val arbitraryHoursUnit       = Arbitrary(Gen.const(CronUnit[Hour.type]))
  implicit lazy val arbitraryDaysOfMonthUnit = Arbitrary(Gen.const(CronUnit[DayOfMonth.type]))
  implicit lazy val arbitraryMonthsUnit      = Arbitrary(Gen.const(CronUnit[Month.type]))
  implicit lazy val arbitraryDaysOfWeekUnit  = Arbitrary(Gen.const(CronUnit[DayOfWeek.type]))

}
