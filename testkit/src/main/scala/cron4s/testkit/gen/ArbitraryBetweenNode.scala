package cron4s.testkit.gen

import cron4s.CronField._

import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryBetweenNode extends NodeGenerators {

  implicit lazy val arbitraryBetweenSecond = Arbitrary(betweenGen[Second])
  implicit lazy val arbitraryBetweenMinute = Arbitrary(betweenGen[Minute])
  implicit lazy val arbitraryBetweenHour = Arbitrary(betweenGen[Hour])
  implicit lazy val arbitraryBetweenDayOfMonth = Arbitrary(betweenGen[DayOfMonth])
  implicit lazy val arbitraryBetweenMonth = Arbitrary(betweenGen[Month])
  implicit lazy val arbitraryBetweenDayOfWeek = Arbitrary(betweenGen[DayOfWeek])

}
