package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitrarySeveralExpr extends ExprGens {

  implicit lazy val arbitrarySeveralSecondExpr = Arbitrary(severalExprGen(CronUnit[Second.type]))
  implicit lazy val arbitrarySeveralMinuteExpr = Arbitrary(severalExprGen(CronUnit[Minute.type]))
  implicit lazy val arbitrarySeveralHourExpr = Arbitrary(severalExprGen(CronUnit[Hour.type]))
  implicit lazy val arbitrarySeveralDayOfMonthExpr = Arbitrary(severalExprGen(CronUnit[DayOfMonth.type]))
  implicit lazy val arbitrarySeveralMonthExpr = Arbitrary(severalExprGen(CronUnit[Month.type]))
  implicit lazy val arbitrarySeveralDayOfWeekExpr = Arbitrary(severalExprGen(CronUnit[DayOfWeek.type]))

}
