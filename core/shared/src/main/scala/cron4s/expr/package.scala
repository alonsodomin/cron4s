/*
 * Copyright 2017 Antonio Alonso Dominguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cron4s

import shapeless._

/**
  * Created by alonsodomin on 04/01/2016.
  */
package object expr {

  private[expr] type RawFieldExpr[F <: CronField] =
    EachNode[F] :+: ConstNode[F] :+: BetweenNode[F] :+: SeveralNode[F] :+: EveryNode[F] :+: CNil

  private[expr] type RawEnumerableExpr[F <: CronField] =
    ConstNode[F] :+: BetweenNode[F] :+: CNil

  private[expr] type RawDivisibleExpr[F <: CronField] =
    EachNode[F] :+: BetweenNode[F] :+: SeveralNode[F] :+: CNil

  type SecondsNode     = FieldExpr[CronField.Second]
  type MinutesNode     = FieldExpr[CronField.Minute]
  type HoursNode       = FieldExpr[CronField.Hour]
  type DaysOfMonthNode = FieldExpr[CronField.DayOfMonth]
  type MonthsNode      = FieldExpr[CronField.Month]
  type DaysOfWeekNode  = FieldExpr[CronField.DayOfWeek]

  private[cron4s] type TimePartAST = SecondsNode :: MinutesNode :: HoursNode :: HNil
  private[cron4s] type DatePartAST = DaysOfMonthNode :: MonthsNode :: DaysOfWeekNode :: HNil

  private[cron4s] type RawCronExpr =
    SecondsNode :: MinutesNode :: HoursNode :: DaysOfMonthNode :: MonthsNode :: DaysOfWeekNode :: HNil

}
