package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryConstNode extends NodeGenerators {

  implicit lazy val arbitraryConstSecond = Arbitrary(constGen(CronUnit[Second]))
  implicit lazy val arbitraryConstMinute = Arbitrary(constGen(CronUnit[Minute]))
  implicit lazy val arbitraryConstHour = Arbitrary(constGen(CronUnit[Hour]))
  implicit lazy val arbitraryConstDayOfMonth = Arbitrary(constGen(CronUnit[DayOfMonth]))
  implicit lazy val arbitraryConstMonth = Arbitrary(constGen(CronUnit[Month]))
  implicit lazy val arbitraryConstDayOfWeek = Arbitrary(constGen(CronUnit[DayOfWeek]))

}
