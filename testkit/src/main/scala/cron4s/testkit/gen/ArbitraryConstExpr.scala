package cron4s.testkit.gen

import cron4s.CronField._
import cron4s.CronUnit
import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ArbitraryConstExpr extends ExprGens {

  implicit lazy val arbitraryConstSecondExpr = Arbitrary(constExprGen(CronUnit[Second.type]))
  implicit lazy val arbitraryConstMinuteExpr = Arbitrary(constExprGen(CronUnit[Minute.type]))
  implicit lazy val arbitraryConstHourExpr = Arbitrary(constExprGen(CronUnit[Hour.type]))
  implicit lazy val arbitraryConstDayOfMonthExpr = Arbitrary(constExprGen(CronUnit[DayOfMonth.type]))
  implicit lazy val arbitraryConstMonthExpr = Arbitrary(constExprGen(CronUnit[Month.type]))
  implicit lazy val arbitraryConstDayOfWeekExpr = Arbitrary(constExprGen(CronUnit[DayOfWeek.type]))

}
