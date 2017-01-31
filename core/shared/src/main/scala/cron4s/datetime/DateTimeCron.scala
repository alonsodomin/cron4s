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

import cron4s.expr.{CronExpr, DateCronExpr, TimeCronExpr}
import cron4s.generic
import cron4s.types.Predicate
import shapeless.Coproduct

import scalaz.PlusEmpty

/**
  * Created by alonsodomin on 14/01/2017.
  */
trait DateTimeCron[T] {

  protected def matches[DateTime](expr: T, adapter: DateTimeAdapter[DateTime])(implicit M: PlusEmpty[Predicate]): Predicate[DateTime]

  def allOf[DateTime](expr: T, adapter: DateTimeAdapter[DateTime]): Predicate[DateTime] =
    matches(expr, adapter)(Predicate.conjunction.monoidK)

  def anyOf[DateTime](expr: T, adapter: DateTimeAdapter[DateTime]): Predicate[DateTime] =
    matches(expr, adapter)(Predicate.disjunction.monoidK)

  def next[DateTime](expr: T, adapter: DateTimeAdapter[DateTime])(from: DateTime): Option[DateTime] = step(expr, adapter)(from, 1)

  def prev[DateTime](expr: T, adapter: DateTimeAdapter[DateTime])(from: DateTime): Option[DateTime] = step(expr, adapter)(from, -1)

  def step[DateTime](expr: T, adapter: DateTimeAdapter[DateTime])(from: DateTime, stepSize: Int): Option[DateTime]

  def ranges(expr: T): List[IndexedSeq[Int]]

}

object DateTimeCron {
  @inline def apply[T, DateTime](implicit ev: DateTimeCron[T]): DateTimeCron[T] = ev

  implicit val fullCronInstance: DateTimeCron[CronExpr]     = new FullCron
  implicit val timeCronInstance: DateTimeCron[TimeCronExpr] = new TimeCron
  implicit val dateCronInstance: DateTimeCron[DateCronExpr] = new DateCron
}

private[datetime] class FullCron extends DateTimeCron[CronExpr] {

  protected def matches[DateTime](expr: CronExpr, adapter: DateTimeAdapter[DateTime])(implicit M: PlusEmpty[Predicate]): Predicate[DateTime] = {
    val reducer = new PredicateReducer[DateTime](adapter)
    reducer.run(Coproduct[AnyCron](expr))
  }

  def step[DateTime](expr: CronExpr, adapter: DateTimeAdapter[DateTime])(from: DateTime, amount: Int): Option[DateTime] = {
    val stepper = new Stepper[DateTime](adapter)
    for {
      (adjustedTime, carryOver) <- stepper.stepOverTime(expr.timePart.raw, from, amount)
      (adjustedDate, _)         <- stepper.stepOverDate(expr.datePart.raw, adjustedTime, carryOver)(allOf(expr, adapter))
    } yield adjustedDate
  }

  def ranges(expr: CronExpr): List[IndexedSeq[Int]] =
    expr.raw.map(generic.ops.range).toList
}

private[datetime] class TimeCron extends DateTimeCron[TimeCronExpr] {

  protected def matches[DateTime](expr: TimeCronExpr, adapter: DateTimeAdapter[DateTime])(implicit M: PlusEmpty[Predicate]): Predicate[DateTime] = {
    val reducer = new PredicateReducer[DateTime](adapter)
    reducer.run(Coproduct[AnyCron](expr))
  }

  def step[DateTime](expr: TimeCronExpr, adapter: DateTimeAdapter[DateTime])(from: DateTime, stepSize: Int): Option[DateTime] = {
    val stepper = new Stepper[DateTime](adapter)
    stepper.stepOverTime(expr.raw, from, stepSize).map(_._1)
  }

  def ranges(expr: TimeCronExpr): List[IndexedSeq[Int]] =
    expr.raw.map(generic.ops.range).toList
}

private[datetime] class DateCron extends DateTimeCron[DateCronExpr] {

  protected def matches[DateTime](expr: DateCronExpr, adapter: DateTimeAdapter[DateTime])(implicit M: PlusEmpty[Predicate]): Predicate[DateTime] = {
    val reducer = new PredicateReducer[DateTime](adapter)
    reducer.run(Coproduct[AnyCron](expr))
  }

  def step[DateTime](expr: DateCronExpr, adapter: DateTimeAdapter[DateTime])(from: DateTime, stepSize: Int): Option[DateTime] = {
    val stepper = new Stepper[DateTime](adapter)
    stepper.stepOverDate(expr.raw, from, stepSize)(allOf(expr, adapter)).map(_._1)
  }

  def ranges(expr: DateCronExpr): List[IndexedSeq[Int]] =
    expr.raw.map(generic.ops.range).toList

}
