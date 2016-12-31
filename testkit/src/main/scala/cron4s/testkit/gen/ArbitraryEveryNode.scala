package cron4s.testkit.gen

import cron4s.CronField._

import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryEveryNode extends NodeGenerators {

  implicit lazy val arbitraryEverySecond = Arbitrary(everyGen[Second])
  implicit lazy val arbitraryEveryMinute = Arbitrary(everyGen[Minute])
  implicit lazy val arbitraryEveryHour = Arbitrary(everyGen[Hour])
  implicit lazy val arbitraryEveryDayOfMonth = Arbitrary(everyGen[DayOfMonth])
  implicit lazy val arbitraryEveryMonth = Arbitrary(everyGen[Month])
  implicit lazy val arbitraryEveryDayOfWeek = Arbitrary(everyGen[DayOfWeek])

}
