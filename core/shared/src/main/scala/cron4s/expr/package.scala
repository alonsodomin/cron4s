package cron4s

import shapeless._

/**
  * Created by alonsodomin on 04/01/2016.
  */
package object expr {

  type MinutesExpr     = Expr[CronField.Minute.type]
  type HoursExpr       = Expr[CronField.Hour.type]
  type DaysOfMonthExpr = Expr[CronField.DayOfMonth.type]
  type MonthsExpr      = Expr[CronField.Month.type]
  type DaysOfWeekExpr  = Expr[CronField.DayOfWeek.type]

  type TimePartRepr = MinutesExpr :: HoursExpr :: HNil
  type DatePartRepr = DaysOfMonthExpr :: MonthsExpr :: DaysOfWeekExpr :: HNil
  type CronExprRepr = MinutesExpr :: HoursExpr :: DaysOfMonthExpr :: MonthsExpr :: DaysOfWeekExpr :: HNil

}
