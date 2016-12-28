package cron4s

import shapeless._

/**
  * Created by alonsodomin on 04/01/2016.
  */
package object expr extends Conversions {

  type FieldNode[F <: CronField] =
    EachNode[F] :+: ConstNode[F] :+: BetweenNode[F] :+: SeveralNode[F] :+: EveryNode[F] :+: CNil

  type SeveralMemberNode[F <: CronField] =
    ConstNode[F] :+: BetweenNode[F] :+: CNil

  type FrequencyBaseNode[F <: CronField] =
    EachNode[F] :+: ConstNode[F] :+: BetweenNode[F] :+: SeveralNode[F] :+: CNil

  type SecondsNode     = FieldNode[CronField.Second]
  type MinutesNode     = FieldNode[CronField.Minute]
  type HoursNode       = FieldNode[CronField.Hour]
  type DaysOfMonthNode = FieldNode[CronField.DayOfMonth]
  type MonthsNode      = FieldNode[CronField.Month]
  type DaysOfWeekNode  = FieldNode[CronField.DayOfWeek]

  private[cron4s] type TimePartAST = SecondsNode :: MinutesNode :: HoursNode :: HNil
  private[cron4s] type DatePartAST = DaysOfMonthNode :: MonthsNode :: DaysOfWeekNode :: HNil
  private[cron4s] type CronExprAST = SecondsNode :: MinutesNode :: HoursNode :: DaysOfMonthNode :: MonthsNode :: DaysOfWeekNode :: HNil

}
