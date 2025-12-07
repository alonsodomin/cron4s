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

package cron4s.datetime

import cats.MonoidK
import cats.instances.list._
import cron4s.CronField
import cron4s.base._
import cron4s.expr._
import cron4s.syntax.predicate._

/**
  * Created by domingueza on 29/07/2016.
  */
private[datetime] final class PredicateReducer[DateTime](DT: IsDateTime[DateTime])(implicit
    M: MonoidK[Predicate]
) {
  private def predicateFor[N[_ <: CronField], F <: CronField](field: F, node: N[F])(implicit
      expr: FieldExpr[N, F]
  ): Predicate[DateTime] =
    Predicate { dt =>
      DT.get(dt, field)
        .map(expr.matches(node))
        .getOrElse(M.empty[DateTime](dt))
    }
  type Predicatable =
    SecondsNode | MinutesNode | HoursNode | DaysOfMonthNode | MonthsNode | DaysOfWeekNode | YearsNode

  type FromRawable = CronExpr | DateCronExpr | TimeCronExpr
  import CronField._
  def fromRaw(t: FromRawable): List[Predicate[DateTime]] = t match {
    case t: CronExpr =>
      t.raw match {
        case (seconds, minutes, hours, daysOfMonth, months, daysOfWeek,years) =>
          List(
            predicateFor(Second, seconds),
            predicateFor(Minute, minutes),
            predicateFor(Hour, hours),
            predicateFor(DayOfMonth, daysOfMonth),
            predicateFor(Month, months),
            predicateFor(DayOfWeek, daysOfWeek),
            predicateFor(Year, years),
          )
      }
    case t: DateCronExpr =>
      t.raw match {
        case (daysOfMonth, months, daysOfWeek,years) =>
          List(
            predicateFor(DayOfMonth, daysOfMonth),
            predicateFor(Month, months),
            predicateFor(DayOfWeek, daysOfWeek),
            predicateFor(Year, years),
          )
      }
    case t: TimeCronExpr =>
      t.raw match {
        case (seconds, minutes, hours) =>
          List(
            predicateFor(Second, seconds),
            predicateFor(Minute, minutes),
            predicateFor(Hour, hours)
          )
      }
  }

  def run(cron: AnyCron): Predicate[DateTime] =
    asOf(fromRaw(cron))
}
