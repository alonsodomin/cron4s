package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitrarySeveralNode extends NodeGenerators {

  implicit lazy val arbitrarySeveralSecond = Arbitrary(severalGen(CronUnit[Second]))
  implicit lazy val arbitrarySeveralMinute = Arbitrary(severalGen(CronUnit[Minute]))
  implicit lazy val arbitrarySeveralHour = Arbitrary(severalGen(CronUnit[Hour]))
  implicit lazy val arbitrarySeveralDayOfMonth = Arbitrary(severalGen(CronUnit[DayOfMonth]))
  implicit lazy val arbitrarySeveralMonth = Arbitrary(severalGen(CronUnit[Month]))
  implicit lazy val arbitrarySeveralDayOfWeek = Arbitrary(severalGen(CronUnit[DayOfWeek]))

}
