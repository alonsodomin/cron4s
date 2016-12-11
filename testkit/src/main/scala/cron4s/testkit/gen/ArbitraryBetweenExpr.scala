package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryBetweenExpr extends ExprGens {

  implicit lazy val arbitraryBetweenSecondExpr = Arbitrary(betweenExprGen(CronUnit[Second]))
  implicit lazy val arbitraryBetweenMinuteExpr = Arbitrary(betweenExprGen(CronUnit[Minute]))
  implicit lazy val arbitraryBetweenHourExpr = Arbitrary(betweenExprGen(CronUnit[Hour]))
  implicit lazy val arbitraryBetweenDayOfMonthExpr = Arbitrary(betweenExprGen(CronUnit[DayOfMonth]))
  implicit lazy val arbitraryBetweenMonthExpr = Arbitrary(betweenExprGen(CronUnit[Month]))
  implicit lazy val arbitraryBetweenDayOfWeekExpr = Arbitrary(betweenExprGen(CronUnit[DayOfWeek]))

}
