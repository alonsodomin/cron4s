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

import cron4s.expr.{CronExpr, DateCronExpr, RawCronExpr, TimeCronExpr}
import cron4s.types.Predicate

import scalaz.{Either3, OneOr, PlusEmpty}

/**
  * Created by alonsodomin on 14/01/2017.
  */
trait DateTimeExpr[T, DateTime] {
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

object DateTimeExpr {
  @inline def apply[T, DateTime](implicit ev: DateTimeExpr[T, DateTime]): DateTimeExpr[T, DateTime] = ev
}

trait DateTimeCron[DateTime] extends DateTimeExpr[CronExpr, DateTime] {

  protected def matches(expr: CronExpr)(implicit M: PlusEmpty[Predicate]): Predicate[DateTime] = {
    val reducer = new PredicateReducer[DateTime]
    reducer.run(Either3.left3(expr.raw))
  }

  override def step(expr: CronExpr)(from: DateTime, stepSize: Int): Option[DateTime] = ???
}

object DateTimeCron {
  def apply[DateTime](implicit adapter0: DateTimeAdapter[DateTime]): DateTimeCron[DateTime] =
    new DateTimeCron[DateTime] { implicit val adapter = adapter0 }
}

trait TimeCron[DateTime] extends DateTimeExpr[TimeCronExpr, DateTime] {

}

trait DateCron[DateTime] extends DateTimeExpr[DateCronExpr, DateTime] {

}
