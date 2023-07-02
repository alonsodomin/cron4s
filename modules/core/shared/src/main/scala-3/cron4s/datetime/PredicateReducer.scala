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
import cron4s.expr._
import cron4s.base._
import cron4s.syntax.predicate._

/**
  * Created by domingueza on 29/07/2016.
  */
private[datetime] final class PredicateReducer[DateTime](DT: IsDateTime[DateTime])(implicit
    M: MonoidK[Predicate]
) {
  type Predicatable =
    SecondsNode | MinutesNode | HoursNode | DaysOfMonthNode | MonthsNode | DaysOfWeekNode

  def asPredicate(t: Predicatable): Predicate[DateTime] = {
    def predicateFor[N[_ <: CronField], F <: CronField](field: F, node: N[F])(implicit
        expr: FieldExpr[N, F]
    ): Predicate[DateTime] =
      Predicate { dt =>
        DT.get(dt, field)
          .map(expr.matches(node))
          .getOrElse(M.empty[DateTime](dt))
      }
    import CronField._
    t match {
      case t: SecondsNode =>
        predicateFor(Second, t)
      case t: MinutesNode     => predicateFor(Minute, t)
      case t: HoursNode       => predicateFor(Hour, t)
      case t: DaysOfMonthNode => predicateFor(DayOfMonth, t)
      case t: MonthsNode      => predicateFor(Month, t)
      case t: DaysOfWeekNode  => predicateFor(DayOfWeek, t)
    }
  }

  type FromRawable = CronExpr | DateCronExpr | TimeCronExpr
  def fromRaw(t: FromRawable): List[Predicate[DateTime]] = t match {
    case t: CronExpr     => t.raw.toList.map(asPredicate)
    case t: DateCronExpr => t.raw.toList.map(asPredicate)
    case t: TimeCronExpr => t.raw.toList.map(asPredicate)
  }

  def run(cron: AnyCron): Predicate[DateTime] =
    asOf(fromRaw(cron))
}
