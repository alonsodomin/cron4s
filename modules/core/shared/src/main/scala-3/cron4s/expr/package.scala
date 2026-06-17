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

/**
  * Created by alonsodomin on 04/01/2016.
  */
package object expr {
  private[expr] type RawFieldNode[F <: CronField] =
    EachNode[F] | ConstNode[F] | BetweenNode[F] | SeveralNode[F] | EveryNode[F]

  private[expr] type RawFieldNodeWithAny[F <: CronField] =
    AnyNode[F] | RawFieldNode[F]

  private[expr] type RawEnumerableNode[F <: CronField] =
    ConstNode[F] | BetweenNode[F]
  extension [F <: CronField](t: RawEnumerableNode[F]) {
    private[cron4s] inline def select[T]: Option[T] = t match
      case t: T => Some(t)
      case _    => None
  }

  private[expr] type RawDivisibleNode[F <: CronField] =
    EachNode[F] | BetweenNode[F] | SeveralNode[F]

  type SecondsNode     = FieldNode[CronField.Second]
  type MinutesNode     = FieldNode[CronField.Minute]
  type HoursNode       = FieldNode[CronField.Hour]
  type DaysOfMonthNode = FieldNodeWithAny[CronField.DayOfMonth]
  type MonthsNode      = FieldNode[CronField.Month]
  type DaysOfWeekNode  = FieldNodeWithAny[CronField.DayOfWeek]
  type YearsNode       = FieldNode[CronField.Year]

  private[cron4s] type RawTimeCronExpr =
    SecondsNode *: MinutesNode *: HoursNode *: EmptyTuple
  private[cron4s] type RawDateCronExpr =
    DaysOfMonthNode *: MonthsNode *: DaysOfWeekNode *: YearsNode *: EmptyTuple

  private[cron4s] type RawCronExpr =
    SecondsNode *: MinutesNode *: HoursNode *: DaysOfMonthNode *: MonthsNode *: DaysOfWeekNode *:
      YearsNode *: EmptyTuple
}
