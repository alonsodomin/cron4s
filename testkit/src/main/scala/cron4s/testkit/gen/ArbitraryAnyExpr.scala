package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryAnyExpr extends ExprGens {

  implicit lazy val arbitraryAnySecondExpr = Arbitrary(anyExprGen(CronUnit[Second.type]))
  implicit lazy val arbitraryAnyMinuteExpr = Arbitrary(anyExprGen(CronUnit[Minute.type]))
  implicit lazy val arbitraryAnyHourExpr = Arbitrary(anyExprGen(CronUnit[Hour.type]))
  implicit lazy val arbitraryAnyDayOfMonthExpr = Arbitrary(anyExprGen(CronUnit[DayOfMonth.type]))
  implicit lazy val arbitraryAnyMonthExpr = Arbitrary(anyExprGen(CronUnit[Month.type]))
  implicit lazy val arbitraryAnyDayOfWeekExpr = Arbitrary(anyExprGen(CronUnit[DayOfWeek.type]))

}
