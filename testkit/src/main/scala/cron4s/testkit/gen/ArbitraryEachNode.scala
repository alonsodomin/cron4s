package cron4s.testkit.gen

import cron4s.CronField._

import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryEachNode extends NodeGenerators {

  implicit lazy val arbitraryEachSecond = Arbitrary(eachGen[Second])
  implicit lazy val arbitraryEachMinute = Arbitrary(eachGen[Minute])
  implicit lazy val arbitraryEachHour = Arbitrary(eachGen[Hour])
  implicit lazy val arbitraryEachDayOfMonth = Arbitrary(eachGen[DayOfMonth])
  implicit lazy val arbitraryEachMonth = Arbitrary(eachGen[Month])
  implicit lazy val arbitraryEachDayOfWeek = Arbitrary(eachGen[DayOfWeek])

}
