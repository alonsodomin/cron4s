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

package cron4s.spi

import cron4s.CronField
import cron4s.expr._
import cron4s.types._
import cron4s.syntax.predicate._

import shapeless._

import scalaz.{Either3, PlusEmpty}
import scalaz.std.list._

/**
  * Created by domingueza on 29/07/2016.
  */
private[spi] final class PredicateReducer[DateTime]
    (implicit M: PlusEmpty[Predicate], adapter: DateTimeAdapter[DateTime]) {

  object asPredicate extends Poly1 {
    import CronField._

    private[this] def predicateFor[F <: CronField]
        (field: F, node: FieldExpr[F])
        (implicit expr: Expr[FieldExpr, F]): Predicate[DateTime] =
      Predicate { dt =>
        adapter.get(dt, field).map(expr.matches(node)).getOrElse(!M.empty[DateTime](dt))
      }

    implicit def caseSeconds     = at[SecondsNode](expr => predicateFor(Second, expr))
    implicit def caseMinutes     = at[MinutesNode](expr => predicateFor(Minute, expr))
    implicit def caseHours       = at[HoursNode](expr => predicateFor(Hour, expr))
    implicit def caseDaysOfMonth = at[DaysOfMonthNode](expr => predicateFor(DayOfMonth, expr))
    implicit def caseMonths      = at[MonthsNode](expr => predicateFor(Month, expr))
    implicit def caseDaysOfWeek  = at[DaysOfWeekNode](expr => predicateFor(DayOfWeek, expr))
  }

  def run(ast: Either3[RawCronExpr, TimePartAST, DatePartAST]): Predicate[DateTime] = {
    val predicateList: List[Predicate[DateTime]] = ast.fold(
      _.map(asPredicate).toList,
      _.map(asPredicate).toList,
      _.map(asPredicate).toList
    )
    asOf(predicateList)
  }

}
