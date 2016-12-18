package cron4s

import shapeless._

/**
  * Created by alonsodomin on 04/01/2016.
  */
package object expr {

  type FieldExpr[F <: CronField] =
    EachExpr[F] :+: ConstExpr[F] :+: BetweenExpr[F] :+: SeveralExpr[F] :+: EveryExpr[F] :+: CNil

  type EnumExpr[F <: CronField] =
    ConstExpr[F] :+: BetweenExpr[F] :+: CNil

  type DivExpr[F <: CronField] =
    EachExpr[F] :+: ConstExpr[F] :+: BetweenExpr[F] :+: SeveralExpr[F] :+: CNil

  type SecondsExpr     = FieldExpr[CronField.Second]
  type MinutesExpr     = FieldExpr[CronField.Minute]
  type HoursExpr       = FieldExpr[CronField.Hour]
  type DaysOfMonthExpr = FieldExpr[CronField.DayOfMonth]
  type MonthsExpr      = FieldExpr[CronField.Month]
  type DaysOfWeekExpr  = FieldExpr[CronField.DayOfWeek]

  private[cron4s] type TimePartRepr = SecondsExpr :: MinutesExpr :: HoursExpr :: HNil
  private[cron4s] type DatePartRepr = DaysOfMonthExpr :: MonthsExpr :: DaysOfWeekExpr :: HNil
  private[cron4s] type CronExprRepr = SecondsExpr :: MinutesExpr :: HoursExpr :: DaysOfMonthExpr :: MonthsExpr :: DaysOfWeekExpr :: HNil

}
