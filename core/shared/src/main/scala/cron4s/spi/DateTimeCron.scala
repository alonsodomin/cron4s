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

import cron4s.expr.{CronExpr, DateCronExpr, TimeCronExpr}
import cron4s.types.Predicate

import shapeless.Coproduct

import scalaz.PlusEmpty

/**
  * Created by alonsodomin on 14/01/2017.
  */
trait DateTimeCron[T, DateTime] {
  implicit def adapter: DateTimeAdapter[DateTime]

  protected def matches(expr: T)(implicit M: PlusEmpty[Predicate]): Predicate[DateTime]

  def allOf(expr: T): Predicate[DateTime] =
    matches(expr)(Predicate.conjunction.monoidK)

  def anyOf(expr: T): Predicate[DateTime] =
    matches(expr)(Predicate.disjunction.monoidK)

  def next(expr: T)(from: DateTime): Option[DateTime] = step(expr)(from, 1)

  def prev(expr: T)(from: DateTime): Option[DateTime] = step(expr)(from, -1)

  def step(expr: T)(from: DateTime, stepSize: Int): Option[DateTime]

}

object DateTimeCron {
  @inline def apply[T, DateTime](implicit ev: DateTimeCron[T, DateTime]): DateTimeCron[T, DateTime] = ev

  implicit def fullCronInstance[DateTime](implicit adapter0: DateTimeAdapter[DateTime]): FullCron[DateTime] =
    new FullCron[DateTime] { implicit val adapter = adapter0 }

  implicit def timeCronInstance[DateTime](implicit adapter0: DateTimeAdapter[DateTime]): TimeCron[DateTime] =
    new TimeCron[DateTime] { implicit val adapter = adapter0 }

  implicit def dateCronInstance[DateTime](implicit adapter0: DateTimeAdapter[DateTime]): DateCron[DateTime] =
    new DateCron[DateTime] { implicit val adapter = adapter0 }
}

trait FullCron[DateTime] extends DateTimeCron[CronExpr, DateTime] {

  protected def matches(expr: CronExpr)(implicit M: PlusEmpty[Predicate]): Predicate[DateTime] = {
    val reducer = new PredicateReducer[DateTime]
    reducer.run(Coproduct[AnyCron](expr))
  }

  def step(expr: CronExpr)(from: DateTime, amount: Int): Option[DateTime] = {
    val stepper = new Stepper[DateTime](from, amount)
    stepper.run(expr)
  }
}

trait TimeCron[DateTime] extends DateTimeCron[TimeCronExpr, DateTime] {

  protected def matches(expr: TimeCronExpr)(implicit M: PlusEmpty[Predicate]): Predicate[DateTime] = {
    val reducer = new PredicateReducer[DateTime]
    reducer.run(Coproduct[AnyCron](expr))
  }

  def step(expr: TimeCronExpr)(from: DateTime, stepSize: Int): Option[DateTime] = ???

}

trait DateCron[DateTime] extends DateTimeCron[DateCronExpr, DateTime] {

  protected def matches(expr: DateCronExpr)(implicit M: PlusEmpty[Predicate]): Predicate[DateTime] = {
    val reducer = new PredicateReducer[DateTime]
    reducer.run(Coproduct[AnyCron](expr))
  }

  def step(expr: DateCronExpr)(from: DateTime, stepSize: Int): Option[DateTime] = ???

}
