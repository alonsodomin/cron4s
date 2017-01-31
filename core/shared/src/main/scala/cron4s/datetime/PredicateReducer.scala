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

import cron4s.CronField
import cron4s.expr._
import cron4s.base._
import cron4s.syntax.predicate._

import shapeless._

import scalaz.PlusEmpty
import scalaz.std.list._

/**
  * Created by domingueza on 29/07/2016.
  */
private[datetime] final class PredicateReducer[DateTime](adapter: DateTimeAdapter[DateTime])
    (implicit M: PlusEmpty[Predicate]) {

  object asPredicate extends Poly1 {
    import CronField._

    private[this] def predicateFor[F <: CronField]
        (field: F, node: FieldNode[F])
        (implicit expr: Expr[FieldNode, F]): Predicate[DateTime] = {
      Predicate { dt =>
        adapter.get(dt, field).map(expr.matches(node)).getOrElse(!M.empty[DateTime](dt))
      }
    }

    implicit def caseSeconds     = at[SecondsNode](node => predicateFor(Second, node))
    implicit def caseMinutes     = at[MinutesNode](node => predicateFor(Minute, node))
    implicit def caseHours       = at[HoursNode](node => predicateFor(Hour, node))
    implicit def caseDaysOfMonth = at[DaysOfMonthNode](node => predicateFor(DayOfMonth, node))
    implicit def caseMonths      = at[MonthsNode](node => predicateFor(Month, node))
    implicit def caseDaysOfWeek  = at[DaysOfWeekNode](node => predicateFor(DayOfWeek, node))
  }

  object fromRaw extends Poly1 {
    implicit def caseFullExpr = at[CronExpr](_.raw.map(asPredicate).toList)
    implicit def caseDateExpr = at[DateCronExpr](_.raw.map(asPredicate).toList)
    implicit def caseTimeExpr = at[TimeCronExpr](_.raw.map(asPredicate).toList)
  }

  def run(cron: AnyCron): Predicate[DateTime] =
    asOf(cron.fold(fromRaw))

}
