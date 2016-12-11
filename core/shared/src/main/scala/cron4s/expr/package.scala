package cron4s

import shapeless._

/**
  * Created by alonsodomin on 04/01/2016.
  */
package object expr {

  type SecondExpr      = Expr[CronField.Second]
  type MinutesExpr     = Expr[CronField.Minute]
  type HoursExpr       = Expr[CronField.Hour]
  type DaysOfMonthExpr = Expr[CronField.DayOfMonth]
  type MonthsExpr      = Expr[CronField.Month]
  type DaysOfWeekExpr  = Expr[CronField.DayOfWeek]

  private[cron4s] type TimePartRepr = SecondExpr :: MinutesExpr :: HoursExpr :: HNil
  private[cron4s] type DatePartRepr = DaysOfMonthExpr :: MonthsExpr :: DaysOfWeekExpr :: HNil
  private[cron4s] type CronExprRepr = SecondExpr :: MinutesExpr :: HoursExpr :: DaysOfMonthExpr :: MonthsExpr :: DaysOfWeekExpr :: HNil

}
