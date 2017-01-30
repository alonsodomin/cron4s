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
import cron4s.expr.Expr
import cron4s.types._
import cron4s.syntax.expr._

trait DateTimeNode[E[_ <: CronField], F <: CronField] {
  implicit def E: Expr[E, F]

  /**
    * Tests if this field expressions matches in the given date-time
    *
    * @return true if there is a field in this date-time that matches this expression
    */
  def matchesIn[DateTime](expr: E[F], adapter: DateTimeAdapter[DateTime]): Predicate[DateTime] = Predicate { dt =>
    val current = adapter.get(dt, expr.unit.field)
    current.map(expr.matches).getOrElse(false)
  }

  /**
    * Calculates the next date-time to a given one considering only the field
    * represented by this expression.
    *
    * @param dateTime date-time used as a reference
    * @return the next date-time
    */
  @inline
  def nextIn[DateTime](expr: E[F], adapter: DateTimeAdapter[DateTime])(dateTime: DateTime): Option[DateTime] =
    stepIn(expr, adapter)(dateTime, 1)

  /**
    * Calculates the previous date-time to a given one considering only the field
    * represented by this expression.
    *
    * @param dateTime date-time used as a reference
    * @return the next date-time
    */
  @inline
  def prevIn[DateTime](expr: E[F], adapter: DateTimeAdapter[DateTime])(dateTime: DateTime): Option[DateTime] =
    stepIn(expr, adapter)(dateTime, -1)

  /**
    * Calculates a date-time that is in either the past or the future relative
    * to a given one, a delta amount and considering only the field represented
    * by this expression.
    *
    * @param dateTime date-time used as a reference
    * @param step step size
    * @return a date-time that is an amount of given steps from the given one
    */
  def stepIn[DateTime](expr: E[F], adapter: DateTimeAdapter[DateTime])(dateTime: DateTime, step: Int): Option[DateTime] = {
    for {
      current  <- adapter.get(dateTime, expr.unit.field)
      newValue <- expr.step(current, step).map(_._1)
      adjusted <- adapter.set(dateTime, expr.unit.field, newValue)
    } yield adjusted
  }

}

object DateTimeNode {

  @inline def apply[E[_ <: CronField], F <: CronField, DateTime]
    (implicit ev: DateTimeNode[E, F]): DateTimeNode[E, F] = ev

  implicit def derive[E[_ <: CronField], F <: CronField, DateTime](
      implicit E0: Expr[E, F], adapter0: DateTimeAdapter[DateTime]): DateTimeNode[E, F] =
    new DateTimeNode[E, F] {
      implicit val E = E0
      implicit val adapter = adapter0
    }

}