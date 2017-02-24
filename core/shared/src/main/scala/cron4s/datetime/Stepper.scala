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
import cron4s.base.Direction
import cron4s.expr._

import shapeless._

import scala.annotation.tailrec

private[datetime] final class Stepper[DateTime](DT: IsDateTime[DateTime]) {
  import CronField._

  protected type Step = Option[(DateTime, Int, Direction)]

  protected[this] def stepField[F <: CronField]
      (expr: FieldNode[F], from: DateTime, step: Int, direction: Direction): Option[(Int, Int)] =
    DT.get(from, expr.unit.field)
      .flatMap(expr.step0(_, step, direction))

  protected[this] def stepAndAdjust[F <: CronField]
      (dateTimeAndStep: Step, expr: FieldNode[F]): Step = {
    for {
      (dateTime, step, dir) <- dateTimeAndStep
      (value, nextStep)     <- stepField(expr, dateTime, step, dir)
      newDateTime           <- DT.set(dateTime, expr.unit.field, value)
    } yield (newDateTime, nextStep, dir)
  }

  protected[this] def stepDayOfWeek
      (dt: DateTime, expr: FieldNode[DayOfWeek], stepSize: Int, direction: Direction): Step = {
    for {
      dayOfWeek         <- DT.get(dt, DayOfWeek)
      (value, nextStep) <- expr.step0(dayOfWeek, stepSize, direction)
      newDateTime       <- DT.set(dt, DayOfWeek, value)
      newDayOfWeek      <- DT.get(dt, DayOfWeek)
    } yield (newDateTime, nextStep + (newDayOfWeek - dayOfWeek), direction)
  }

  object stepping extends Poly2 {
    implicit def caseSeconds     = at[Step, SecondsNode](stepAndAdjust)
    implicit def caseMinutes     = at[Step, MinutesNode](stepAndAdjust)
    implicit def caseHours       = at[Step, HoursNode](stepAndAdjust)
    implicit def caseDaysOfMonth = at[Step, DaysOfMonthNode](stepAndAdjust)
    implicit def caseMonths      = at[Step, MonthsNode](stepAndAdjust)
  }

  def stepOverDate(rawExpr: RawDateCronExpr, from: DateTime, step: Int, direction: Direction)(matches: DateTime => Boolean): Step = {
    val dateWithoutDayOfWeek = rawExpr.take(2)
    val daysOfWeek = rawExpr.select[DaysOfWeekNode]

    def doStep(previous: Step): Step =
      dateWithoutDayOfWeek.foldLeft(previous)(stepping).flatMap {
        case (dt, stepSize, dir) => stepDayOfWeek(dt, daysOfWeek, stepSize, dir)
      }

    @tailrec
    def dateStepLoop(previous: Step): Step = {
      val dateAdjusted = doStep(previous)
      dateAdjusted match {
        case Some((_, nextStep, _)) if nextStep != 0 =>
          dateStepLoop(doStep(dateAdjusted))

        case Some((dateTime, _, dir)) if !matches(dateTime) =>
          val nextStep: Step = Some((dateTime, 1, dir))
          doStep(nextStep)

        case _ => dateAdjusted
      }
    }

    val initialStep: Step = Some((from, step, direction))
    dateStepLoop(initialStep)
  }

  def stepOverTime(rawExpr: RawTimeCronExpr, from: DateTime, initial: Int, dir: Direction): Step =
    rawExpr.foldLeft(Some((from, initial, dir)): Step)(stepping)

}
