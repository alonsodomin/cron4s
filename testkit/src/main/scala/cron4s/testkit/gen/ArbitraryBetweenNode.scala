package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryBetweenNode extends NodeGenerators {

  implicit lazy val arbitraryBetweenSecond = Arbitrary(betweenGen(CronUnit[Second]))
  implicit lazy val arbitraryBetweenMinute = Arbitrary(betweenGen(CronUnit[Minute]))
  implicit lazy val arbitraryBetweenHour = Arbitrary(betweenGen(CronUnit[Hour]))
  implicit lazy val arbitraryBetweenDayOfMonth = Arbitrary(betweenGen(CronUnit[DayOfMonth]))
  implicit lazy val arbitraryBetweenMonth = Arbitrary(betweenGen(CronUnit[Month]))
  implicit lazy val arbitraryBetweenDayOfWeek = Arbitrary(betweenGen(CronUnit[DayOfWeek]))

}
