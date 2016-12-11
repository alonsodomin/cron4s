package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitrarySeveralExpr extends ExprGens {

  implicit lazy val arbitrarySeveralSecondExpr = Arbitrary(severalExprGen(CronUnit[Second]))
  implicit lazy val arbitrarySeveralMinuteExpr = Arbitrary(severalExprGen(CronUnit[Minute]))
  implicit lazy val arbitrarySeveralHourExpr = Arbitrary(severalExprGen(CronUnit[Hour]))
  implicit lazy val arbitrarySeveralDayOfMonthExpr = Arbitrary(severalExprGen(CronUnit[DayOfMonth]))
  implicit lazy val arbitrarySeveralMonthExpr = Arbitrary(severalExprGen(CronUnit[Month]))
  implicit lazy val arbitrarySeveralDayOfWeekExpr = Arbitrary(severalExprGen(CronUnit[DayOfWeek]))

}
