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

import cron4s.expr.CronExpr
import cron4s.types.Predicate

import shapeless.Coproduct

import scalaz.PlusEmpty

/**
  * Base abstraction used to provide support for date-time libraries.
  *
  * @author Antonio Alonso Dominguez
  */
abstract class CronDateTimeOps[DateTime: DateTimeAdapter](expr: CronExpr) {

  private[this] def matches(implicit M: PlusEmpty[Predicate]): Predicate[DateTime] = {
    val reducer = new PredicateReducer[DateTime]
    reducer.run(Coproduct[AST](expr.ast))
  }

  /**
    * Matches all the CRON expression fields against a given date-time
    *
    * @return true if all fields match, false otherwise
    */
  def allOf: Predicate[DateTime] =
    matches(Predicate.conjunction)

  /**
    * Tests whether some of the CRON expression fields matches against a given
    * date-time
    *
    * @return true if any of the fields matches, false otherwise
    */
  def anyOf: Predicate[DateTime] =
    matches(Predicate.disjunction)

  /**
    * Calculates the next date-time to a given one according to this expression
    *
    * @param from date-time used as a reference
    * @return next date-time to a given one according to this expression
    */
  @inline
  def next(from: DateTime): Option[DateTime] = step(from, 1)

  /**
    * Calculates the previous date-time to a given one according to this expression
    *
    * @param from date-time used as a reference
    * @return previous date-time to a given one according to this expression
    */
  @inline
  def prev(from: DateTime): Option[DateTime] = step(from, -1)

  /**
    * Calculates a date-time that is in either the past or the future relative
    * to a given one and a delta amount.
    *
    * @param from date-time used as a reference
    * @param amount delta position from the given date-time. Positive values
    *               represent the future, negative values the past
    * @return a date-time that is an amount of given steps from the given one
    */
  def step(from: DateTime, amount: Int): Option[DateTime] = {
    val stepper = new Stepper[DateTime](from, amount)
    stepper.run(expr)
  }

}
