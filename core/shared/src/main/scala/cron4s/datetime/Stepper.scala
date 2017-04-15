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
import cron4s.base.{Direction, Step}
import cron4s.expr._

import shapeless._

private[datetime] final class Stepper[DateTime](DT: IsDateTime[DateTime]) {

  val MaxIterationCount = 3

  private type ResetPrevFn = DateTime => Option[DateTime]
  private type StepST = Option[(ResetPrevFn, DateTime, Step)]

  private[this] def stepNode[N[_ <: CronField], F <: CronField]
      (stepState: StepST, node: N[F])(implicit expr: FieldExpr[N, F]): StepST =
    stepState.flatMap { case (resetPrevious, from, step) =>
      def resetThis: DateTime => Option[DateTime] = { dt =>
        val resetValue = step.direction match {
          case Direction.Forward   => node.min
          case Direction.Backwards => node.max
        }

        DT.set(dt, node.unit.field, resetValue)
      }

      DT.get(from, node.unit.field).flatMap { currentValue =>
        node.step(currentValue, step) match {
          case Some((newValue, carryOver)) =>
            resetPrevious(from)
              .flatMap(DT.set(_, node.unit.field, newValue))
              .map(dt => (resetThis, dt, step.copy(amount = carryOver)))

          case None =>
            Some((resetThis, from, step.copy(amount = 0)))
        }
      }
  }

  private[this] def stepOverMonth(prev: StepST, expr: MonthsNode): StepST = {
    for {
      (rstFn, dt, s @ Step(carryOver, _)) <- stepNode(prev, expr)
      newDateTime                         <- DT.plus(dt, carryOver * 12, DateTimeUnit.Months)
    } yield (rstFn, newDateTime, s.copy(amount = 0))
  }

  private[this] def stepOverDayOfWeek(prev: StepST, expr: DaysOfWeekNode): StepST = for {
    (rstFn, dt, s @ Step(carryOver, _)) <- stepNode(prev, expr)
    newDateTime                         <- DT.plus(dt, carryOver, DateTimeUnit.Weeks)
  } yield (rstFn, newDateTime, s.copy(amount = 0))

  object stepPerNode extends Poly2 {
    implicit def caseSeconds     = at[StepST, SecondsNode]((step, node) => stepNode(step, node))
    implicit def caseMinutes     = at[StepST, MinutesNode]((step, node) => stepNode(step, node))
    implicit def caseHours       = at[StepST, HoursNode]((step, node) => stepNode(step, node))
    implicit def caseDaysOfMonth = at[StepST, DaysOfMonthNode]((step, node) => stepNode(step, node))
    implicit def caseMonths      = at[StepST, MonthsNode](stepOverMonth)
    implicit def caseDaysOfWeek  = at[StepST, DaysOfWeekNode](stepOverDayOfWeek)
  }

  object foldInternalExpr extends Poly2 {
    implicit def caseFullExpr = at[StepST, CronExpr]((stepSt, expr) => expr.raw.foldLeft(stepSt)(stepPerNode))
    implicit def caseDateExpr = at[StepST, DateCronExpr]((stepSt, expr) => expr.raw.foldLeft(stepSt)(stepPerNode))
    implicit def caseTimeExpr = at[StepST, TimeCronExpr]((stepSt, expr) => expr.raw.foldLeft(stepSt)(stepPerNode))
  }

  def run(cron: AnyCron, from: DateTime, step: Step): Option[DateTime] = {
    val initial: StepST = Some((Some(_), from, step))
    cron.foldLeft(initial)(foldInternalExpr).map(_._2)
  }

}
