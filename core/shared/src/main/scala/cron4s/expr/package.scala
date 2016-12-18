package cron4s

import shapeless._

/**
  * Created by alonsodomin on 04/01/2016.
  */
package object expr {

  type FieldExprAST[F <: CronField] =
    EachExpr[F] :+: ConstExpr[F] :+: BetweenExpr[F] :+: SeveralExpr[F] :+: EveryExpr[F] :+: CNil

  type EnumExprAST[F <: CronField] =
    ConstExpr[F] :+: BetweenExpr[F] :+: CNil

  type DivExprAST[F <: CronField] =
    EachExpr[F] :+: ConstExpr[F] :+: BetweenExpr[F] :+: SeveralExpr[F] :+: CNil

  type SecondsAST     = FieldExprAST[CronField.Second]
  type MinutesAST     = FieldExprAST[CronField.Minute]
  type HoursAST       = FieldExprAST[CronField.Hour]
  type DaysOfMonthAST = FieldExprAST[CronField.DayOfMonth]
  type MonthsAST      = FieldExprAST[CronField.Month]
  type DaysOfWeekAST  = FieldExprAST[CronField.DayOfWeek]

  private[cron4s] type TimePartAST = SecondsAST :: MinutesAST :: HoursAST :: HNil
  private[cron4s] type DatePartAST = DaysOfMonthAST :: MonthsAST :: DaysOfWeekAST :: HNil
  private[cron4s] type CronExprAST = SecondsAST :: MinutesAST :: HoursAST :: DaysOfMonthAST :: MonthsAST :: DaysOfWeekAST :: HNil

}
