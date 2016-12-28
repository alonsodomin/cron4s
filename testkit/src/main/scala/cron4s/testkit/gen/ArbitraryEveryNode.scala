package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryEveryNode extends NodeGenerators {

  implicit lazy val arbitraryEverySecond = Arbitrary(everyGen(CronUnit[Second]))
  implicit lazy val arbitraryEveryMinute = Arbitrary(everyGen(CronUnit[Minute]))
  implicit lazy val arbitraryEveryHour = Arbitrary(everyGen(CronUnit[Hour]))
  implicit lazy val arbitraryEveryDayOfMonth = Arbitrary(everyGen(CronUnit[DayOfMonth]))
  implicit lazy val arbitraryEveryMonth = Arbitrary(everyGen(CronUnit[Month]))
  implicit lazy val arbitraryEveryDayOfWeek = Arbitrary(everyGen(CronUnit[DayOfWeek]))

}
