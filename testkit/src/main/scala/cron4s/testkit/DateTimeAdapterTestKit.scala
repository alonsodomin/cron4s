package cron4s.testkit

import cron4s.spi.DateTimeAdapter
import cron4s.{CronField, CronUnit}
import cron4s.testkit.discipline.DateTimeAdapterTests
import cron4s.testkit.gen.ArbitraryCronFieldValues

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
abstract class DateTimeAdapterTestKit[DateTime <: AnyRef : DateTimeAdapter : Equal](name: String)
  extends FunSuite with Discipline with ArbitraryCronFieldValues with ExtensionsTestKitBase[DateTime] {
  import CronField._
  import CronUnit._

  implicit lazy val arbitraryDateTime = Arbitrary(for {
    seconds     <- Gen.choose(Seconds.min, Seconds.max)
    minutes     <- Gen.choose(Minutes.min, Minutes.max)
    hours       <- Gen.choose(Hours.min, Hours.max)
    daysOfMonth <- Gen.choose(DaysOfMonth.min, DaysOfMonth.max)
    months      <- Gen.const(1)
    daysOfWeek  <- Gen.choose(DaysOfWeek.min, DaysOfWeek.max)
  } yield createDateTime(seconds, minutes, hours, daysOfMonth, months, daysOfWeek))

  checkAll(s"DateTimeAdapter[$name, Second]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Second])
  checkAll(s"DateTimeAdapter[$name, Minute]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Minute])
  checkAll(s"DateTimeAdapter[$name, Hour]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Hour])
  checkAll(s"DateTimeAdapter[$name, DayOfMonth]", DateTimeAdapterTests[DateTime].dateTimeAdapter[DayOfMonth])
  checkAll(s"DateTimeAdapter[$name, Month]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Month])
  checkAll(s"DateTimeAdapter[$name, DayOfWeek]", DateTimeAdapterTests[DateTime].dateTimeAdapter[DayOfWeek])

}
