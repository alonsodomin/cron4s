package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryEachNode extends NodeGenerators {

  implicit lazy val arbitraryEachSecond = Arbitrary(eachGen(CronUnit[Second]))
  implicit lazy val arbitraryEachMinute = Arbitrary(eachGen(CronUnit[Minute]))
  implicit lazy val arbitraryEachHour = Arbitrary(eachGen(CronUnit[Hour]))
  implicit lazy val arbitraryEachDayOfMonth = Arbitrary(eachGen(CronUnit[DayOfMonth]))
  implicit lazy val arbitraryEachMonth = Arbitrary(eachGen(CronUnit[Month]))
  implicit lazy val arbitraryEachDayOfWeek = Arbitrary(eachGen(CronUnit[DayOfWeek]))

}
