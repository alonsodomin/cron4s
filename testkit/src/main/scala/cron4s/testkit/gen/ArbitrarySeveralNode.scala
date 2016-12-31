package cron4s.testkit.gen

import cron4s.CronField._

import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitrarySeveralNode extends NodeGenerators {

  implicit lazy val arbitrarySeveralSecond = Arbitrary(severalGen[Second])
  implicit lazy val arbitrarySeveralMinute = Arbitrary(severalGen[Minute])
  implicit lazy val arbitrarySeveralHour = Arbitrary(severalGen[Hour])
  implicit lazy val arbitrarySeveralDayOfMonth = Arbitrary(severalGen[DayOfMonth])
  implicit lazy val arbitrarySeveralMonth = Arbitrary(severalGen[Month])
  implicit lazy val arbitrarySeveralDayOfWeek = Arbitrary(severalGen[DayOfWeek])

}
