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

import cron4s._
import cron4s.base.Step
import cron4s.expr._

import shapeless._

import scala.annotation.tailrec

private[datetime] final class Stepper[DateTime](DT: IsDateTime[DateTime]) {

  val MaxIterationCount = 3

  protected type DTStep = Option[(DateTime, Step)]

  protected[this] def stepField[N[_ <: CronField], F <: CronField]
      (expr: N[F], from: DateTime, step: Step)(implicit N: FieldExpr[N, F]): Option[(Int, Int)] =
    DT.get(from, expr.unit.field).flatMap(expr.step(_, step))

  protected[this] def stepAndAdjust[N[_ <: CronField], F <: CronField]
      (dateTimeAndStep: DTStep, expr: N[F])(implicit N: FieldExpr[N, F]): DTStep = {
    for {
      (dateTime, step)   <- dateTimeAndStep
      (value, carryOver) <- stepField(expr, dateTime, step)
      newDateTime        <- DT.set(dateTime, expr.unit.field, value)
    } yield (newDateTime, step.copy(amount = carryOver))
  }

  protected[this] def stepOverMonth(prev: DTStep, expr: MonthsNode): DTStep = {
    for {
      (dt, s @ Step(carryOver, _)) <- stepAndAdjust(prev, expr)
      newDateTime                  <- DT.plus(dt, carryOver * 12, DateTimeUnit.Months)
    } yield (newDateTime, s.copy(amount = 0))
  }

  protected[this] def stepOverDayOfWeek(prev: DTStep, expr: DaysOfWeekNode): DTStep = for {
    (dt, s @ Step(carryOver, _)) <- stepAndAdjust(prev, expr)
    newDateTime                  <- DT.plus(dt, carryOver, DateTimeUnit.Weeks)
  } yield (newDateTime, s.copy(amount = 0))

  object stepping extends Poly2 {
    implicit def caseSeconds     = at[DTStep, SecondsNode]((step, node) => stepAndAdjust(step, node))
    implicit def caseMinutes     = at[DTStep, MinutesNode]((step, node) => stepAndAdjust(step, node))
    implicit def caseHours       = at[DTStep, HoursNode]((step, node) => stepAndAdjust(step, node))
    implicit def caseDaysOfMonth = at[DTStep, DaysOfMonthNode]((step, node) => stepAndAdjust(step, node))
    implicit def caseMonths      = at[DTStep, MonthsNode](stepOverMonth)
    implicit def caseDaysOfWeek  = at[DTStep, DaysOfWeekNode](stepOverDayOfWeek)
  }

  def stepOverDate(rawExpr: RawDateCronExpr, from: DateTime, step: Step)(matches: DateTime => Boolean): DTStep = {
    // Steps in the CronExpr and applies carry over from day of week in number of weeks
    def doStep(previous: DTStep): DTStep =
      rawExpr.foldLeft(previous)(stepping)

    @tailrec
    def dateStepLoop(previous: DTStep, iterationCount: Int): DTStep = {
      val dateAdjusted = doStep(previous)

      dateAdjusted match {
        case Some((_, Step(nextStep, _))) if nextStep != 0 && iterationCount < MaxIterationCount =>
          dateStepLoop(dateAdjusted, iterationCount + 1)

        case Some((dateTime, _)) if !matches(dateTime) && iterationCount < MaxIterationCount =>
          val nextStep: DTStep = Some((dateTime, Step(1)))
          dateStepLoop(nextStep, iterationCount + 1)

        case _ => dateAdjusted
      }
    }

    val initialStep: DTStep = Some((from, step))
    dateStepLoop(initialStep, 0)
  }

  def stepOverTime(rawExpr: RawTimeCronExpr, from: DateTime, step: Step): DTStep =
    rawExpr.foldLeft(Some((from, step)): DTStep)(stepping)

}
