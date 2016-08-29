package cron4s.testkit

import cron4s.CronField
import cron4s.ext.DateTimeAdapter
import cron4s.testkit.discipline.DateTimeAdapterTests
import cron4s.testkit.gen.ArbitraryCronFieldValues

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
abstract class DateTimeAdapterTestKit[DateTime <: AnyRef : DateTimeAdapter : Equal : Arbitrary](name: String)
  extends FunSuite with Discipline with ArbitraryCronFieldValues {
  import CronField._

  implicit lazy val arbitraryCronField = Arbitrary(Gen.oneOf(CronField.All))

  checkAll(s"DateTimeAdapter[$name, Second]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Second.type])
  checkAll(s"DateTimeAdapter[$name, Minute]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Minute.type])
  checkAll(s"DateTimeAdapter[$name, Hour]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Hour.type])
  checkAll(s"DateTimeAdapter[$name, DayOfMonth]", DateTimeAdapterTests[DateTime].dateTimeAdapter[DayOfMonth.type])
  checkAll(s"DateTimeAdapter[$name, Month]", DateTimeAdapterTests[DateTime].dateTimeAdapter[Month.type])
  checkAll(s"DateTimeAdapter[$name, DayOfWeek]", DateTimeAdapterTests[DateTime].dateTimeAdapter[DayOfWeek.type])

}
