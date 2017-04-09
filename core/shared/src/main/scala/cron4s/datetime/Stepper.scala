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

  val MaxIterationCount = 3

  protected type DTStep = Option[(DateTime, Int, Direction)]

  protected[this] def stepField[F <: CronField]
      (expr: FieldNode[F], from: DateTime, step: Int, direction: Direction): Option[(Int, Int)] =
    DT.get(from, expr.unit.field)
      .flatMap(expr.step0(_, step, direction))

  protected[this] def stepAndAdjust[F <: CronField]
      (dateTimeAndStep: DTStep, expr: FieldNode[F]): DTStep = {
    for {
      (dateTime, step, dir) <- dateTimeAndStep
      (value, nextStep)     <- stepField(expr, dateTime, step, dir)
      newDateTime           <- DT.set(dateTime, expr.unit.field, value)
    } yield (newDateTime, nextStep, dir)
  }

  protected[this] def stepOverMonth(prev: DTStep, expr: MonthsNode): DTStep = {
    def adjustYear(dt: DateTime, carryOver: Int): Option[DateTime] = {
      if (carryOver == 0) Some(dt)
      else DT.plus(dt, carryOver * 12, CronUnit.Months)
    }

    for {
      (dt, carryOver, dir) <- stepAndAdjust(prev, expr)
      newDateTime          <- adjustYear(dt, carryOver)
    } yield (newDateTime, 0, dir)
  }

  protected[this] def stepOverDayOfWeek(prev: DTStep, expr: DaysOfWeekNode): DTStep = for {
    (dt, carryOver, dir) <- stepAndAdjust(prev, expr)
    newDateTime          <- DT.plus(dt, carryOver * 7, CronUnit.DaysOfMonth)
  } yield (newDateTime, 0, dir)

  object stepping extends Poly2 {
    implicit def caseSeconds     = at[DTStep, SecondsNode](stepAndAdjust)
    implicit def caseMinutes     = at[DTStep, MinutesNode](stepAndAdjust)
    implicit def caseHours       = at[DTStep, HoursNode](stepAndAdjust)
    implicit def caseDaysOfMonth = at[DTStep, DaysOfMonthNode](stepAndAdjust)
    implicit def caseMonths      = at[DTStep, MonthsNode](stepOverMonth)
    implicit def caseDaysOfWeek  = at[DTStep, DaysOfWeekNode](stepOverDayOfWeek)
  }

  def stepOverDate(rawExpr: RawDateCronExpr, from: DateTime, step: Int, direction: Direction)(matches: DateTime => Boolean): DTStep = {
    // Steps in the CronExpr and applies carry over from day of week in number of weeks
    def doStep(previous: DTStep): DTStep =
      rawExpr.foldLeft(previous)(stepping)

    @tailrec
    def dateStepLoop(previous: DTStep, iterationCount: Int): DTStep = {
      val dateAdjusted = doStep(previous)

      dateAdjusted match {
        case Some((_, nextStep, _)) if nextStep != 0 && iterationCount < MaxIterationCount =>
          dateStepLoop(dateAdjusted, iterationCount + 1)

        case Some((dateTime, _, dir)) if !matches(dateTime) && iterationCount < MaxIterationCount =>
          val nextStep: DTStep = Some((dateTime, 1, dir))
          dateStepLoop(nextStep, iterationCount + 1)

        case _ => dateAdjusted
      }
    }

    val initialStep: DTStep = Some((from, step, direction))
    dateStepLoop(initialStep, 0)
  }

  def stepOverTime(rawExpr: RawTimeCronExpr, from: DateTime, initial: Int, dir: Direction): DTStep =
    rawExpr.foldLeft(Some((from, initial, dir)): DTStep)(stepping)

}
