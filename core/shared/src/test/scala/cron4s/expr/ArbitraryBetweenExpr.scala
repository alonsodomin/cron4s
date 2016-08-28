package cron4s.expr

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryBetweenExpr extends ExprGens {

  implicit lazy val arbitraryBetweenMinuteExpr = Arbitrary(betweenExprGen(CronUnit[Minute.type]))
  implicit lazy val arbitraryBetweenHourExpr = Arbitrary(betweenExprGen(CronUnit[Hour.type]))
  implicit lazy val arbitraryBetweenDayOfMonthExpr = Arbitrary(betweenExprGen(CronUnit[DayOfMonth.type]))
  implicit lazy val arbitraryBetweenMonthExpr = Arbitrary(betweenExprGen(CronUnit[Month.type]))
  implicit lazy val arbitraryBetweenDayOfWeekExpr = Arbitrary(betweenExprGen(CronUnit[DayOfWeek.type]))

}
