package cron4s.testkit.gen

import cron4s.{CronField, CronUnit}
import cron4s.testkit.CronFieldValue
import cron4s.types.HasCronField
import cron4s.types.syntax.field._

import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait ArbitraryCronFieldValues {
  import CronField._
  import CronUnit._

  def cronFieldValueGen[F <: CronField](unit: CronUnit[F])(implicit ev: HasCronField[CronUnit, F]): Gen[CronFieldValue[F]] =
    Gen.choose(unit.min, unit.max).map(v => CronFieldValue(unit.field, v))

  implicit lazy val arbitrarySecondValue: Arbitrary[CronFieldValue[Second.type]] =
    Arbitrary(cronFieldValueGen(Seconds))
  implicit lazy val arbitraryMinuteValue: Arbitrary[CronFieldValue[Minute.type]] =
    Arbitrary(cronFieldValueGen(Minutes))
  implicit lazy val arbitraryHourValue: Arbitrary[CronFieldValue[Hour.type]] =
    Arbitrary(cronFieldValueGen(Hours))
  implicit lazy val arbitraryDayOfMonthValue: Arbitrary[CronFieldValue[DayOfMonth.type]] =
    Arbitrary(cronFieldValueGen(DaysOfMonth))
  implicit lazy val arbitraryMonthValue: Arbitrary[CronFieldValue[Month.type]] =
    Arbitrary(cronFieldValueGen(Months))
  implicit lazy val arbitraryDayOfWeekValue: Arbitrary[CronFieldValue[DayOfWeek.type]] =
    Arbitrary(cronFieldValueGen(DaysOfWeek))

}
