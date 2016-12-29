package cron4s.testkit.gen

import cron4s.CronField._

import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryConstNode extends NodeGenerators {

  implicit lazy val arbitraryConstSecond = Arbitrary(constGen[Second])
  implicit lazy val arbitraryConstMinute = Arbitrary(constGen[Minute])
  implicit lazy val arbitraryConstHour = Arbitrary(constGen[Hour])
  implicit lazy val arbitraryConstDayOfMonth = Arbitrary(constGen[DayOfMonth])
  implicit lazy val arbitraryConstMonth = Arbitrary(constGen[Month])
  implicit lazy val arbitraryConstDayOfWeek = Arbitrary(constGen[DayOfWeek])

}
