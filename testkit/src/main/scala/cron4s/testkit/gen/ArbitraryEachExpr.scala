package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryEachExpr extends ExprGens {

  implicit lazy val arbitraryEachSecondExpr = Arbitrary(eachExprGen(CronUnit[Second]))
  implicit lazy val arbitraryEachMinuteExpr = Arbitrary(eachExprGen(CronUnit[Minute]))
  implicit lazy val arbitraryEachHourExpr = Arbitrary(eachExprGen(CronUnit[Hour]))
  implicit lazy val arbitraryEachDayOfMonthExpr = Arbitrary(eachExprGen(CronUnit[DayOfMonth]))
  implicit lazy val arbitraryEachMonthExpr = Arbitrary(eachExprGen(CronUnit[Month]))
  implicit lazy val arbitraryEachDayOfWeekExpr = Arbitrary(eachExprGen(CronUnit[DayOfWeek]))

}
