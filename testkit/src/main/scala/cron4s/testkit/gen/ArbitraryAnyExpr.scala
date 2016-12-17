package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryAnyExpr extends ExprGens {

  implicit lazy val arbitraryAnySecondExpr = Arbitrary(eachExprGen(CronUnit[Second]))
  implicit lazy val arbitraryAnyMinuteExpr = Arbitrary(eachExprGen(CronUnit[Minute]))
  implicit lazy val arbitraryAnyHourExpr = Arbitrary(eachExprGen(CronUnit[Hour]))
  implicit lazy val arbitraryAnyDayOfMonthExpr = Arbitrary(eachExprGen(CronUnit[DayOfMonth]))
  implicit lazy val arbitraryAnyMonthExpr = Arbitrary(eachExprGen(CronUnit[Month]))
  implicit lazy val arbitraryAnyDayOfWeekExpr = Arbitrary(eachExprGen(CronUnit[DayOfWeek]))

}
