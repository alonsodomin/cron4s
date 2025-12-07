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

import cats.syntax.either._

import cron4s._
import cron4s.base.{Direction, Step}
import cron4s.expr._

import scala.annotation.tailrec

private[datetime] final class Stepper[DateTime](DT: IsDateTime[DateTime]) {
  private type ResetPrevFn = DateTime => Option[DateTime]
  private type StepST      = Option[(ResetPrevFn, DateTime, Step)]

  private val identityReset: ResetPrevFn = Some(_)

  private[this] def stepNode[N[_ <: CronField], F <: CronField](stepState: StepST, node: N[F])(
      implicit expr: FieldExpr[N, F]
  ): StepST = {
    def attemptSet(
        dt: DateTime,
        step: Step,
        newValue: Int,
        carryOver: Int
    ): Option[(DateTime, Int)] =
      DT.set(dt, node.unit.field, newValue)
        .map(_ -> carryOver)
        .recover {
          case InvalidFieldValue(_, _) =>
            val newCarryOver = step.direction match {
              case Direction.Forward   => Math.max(carryOver, step.direction.sign)
              case Direction.Backwards =>
                Math.min(carryOver, step.direction.sign)
            }
            dt -> newCarryOver
        }
        .toOption

    stepState.flatMap {
      case (resetPrevious, from, step) =>
        def resetThis: DateTime => Option[DateTime] = {
          val resetValue = step.direction match {
            case Direction.Forward   => node.min
            case Direction.Backwards => node.max
          }

          resetPrevious.andThen(_.flatMap(DT.set(_, node.unit.field, resetValue).toOption))
        }

        DT.get(from, node.unit.field).toOption.flatMap { currentValue =>
          node.step(currentValue, step) match {
            case Some((newValue, carryOver)) =>
              // Attempt to set a new value in the field and reset previous fields
              attemptSet(from, step, newValue, carryOver)
                .flatMap { case (dt, co) => resetPrevious(dt).map(_ -> co) }
                .map {
                  case (dt, co) =>
                    (resetThis, dt, step.copy(amount = Math.abs(co)))
                }

            case None =>
              Some((resetThis, from, step.copy(amount = 0)))
          }
        }
    }
  }

  private[this] def stepOverMonth(prev: StepST, expr: MonthsNode): StepST =
    for {
      (_, dt, s @ Step(carryOver, dir)) <- stepNode(prev, expr)
      newDateTime <- DT.plus(dt, carryOver * 12 * dir.sign, DateTimeUnit.Months)
    } yield (identityReset, newDateTime, s.copy(amount = 0))

  private[this] def stepOverDayOfWeek(prev: StepST, expr: DaysOfWeekNode): StepST =
    for {
      (_, dt, s @ Step(carryOver, dir)) <- stepNode(prev, expr)
      newDateTime                       <- DT.plus(dt, carryOver * dir.sign, DateTimeUnit.Weeks)
    } yield (identityReset, newDateTime, s.copy(amount = 0))

  type FoldInternalExprable = CronExpr | DateCronExpr | TimeCronExpr
  def foldInternalExpr(
      stepSt: StepST,
      expr: FoldInternalExprable
  ): Option[(ResetPrevFn, DateTime, Step)] = expr match {
    case expr: CronExpr =>
      val (dom, mt)              = expr.datePart.raw.take(2)
      val (_, _, daysOfWeekNode,_) = expr.datePart.raw

      for {
        st @ (resetTime, _, _) <- foldInternalExpr(stepSt, expr.timePart)
        (_, dt, step)          <-
          List(
            (step: StepST) => stepNode(step, dom),
            (step: StepST) => stepOverMonth(step, mt)
          ).foldLeft(Some(st): StepST) { case (step, f) => f(step) }
        result <- stepOverDayOfWeek(Some((resetTime, dt, step)), daysOfWeekNode)
      } yield result
    case expr: DateCronExpr =>
      expr.raw match {
        case (daysOfMonth, month, daysOfWeek,year) =>
          List(
            (step: StepST) => stepNode(step, daysOfMonth),
            (step: StepST) => stepOverMonth(step, month),
            (step: StepST) => stepOverDayOfWeek(step, daysOfWeek),
            (step: StepST) => stepNode(step, year)
          ).foldLeft(stepSt) { case (step, f) => f(step) }
      }
    case expr: TimeCronExpr =>
      expr.raw match {
        case (seconds, minutes, hours) =>
          List(
            (step: StepST) => stepNode(step, seconds),
            (step: StepST) => stepNode(step, minutes),
            (step: StepST) => stepNode(step, hours)
          ).foldLeft(stepSt) { case (step, f) => f(step) }
      }
  }

  def run(cron: AnyCron, from: DateTime, step: Step): Option[DateTime] = {
    def initial(dt: DateTime): StepST =
      Some((identityReset, dt, step.copy(amount = 1)))

    @tailrec
    def go(stepSt: StepST, iteration: Int): StepST =
      if (iteration == step.amount) stepSt
      else
        foldInternalExpr(stepSt, cron) match {
          case Some((_, dt, _)) =>
            go(initial(dt), iteration + 1)
          case None => None
        }

    go(initial(from), 0).map(_._2)
  }
}
