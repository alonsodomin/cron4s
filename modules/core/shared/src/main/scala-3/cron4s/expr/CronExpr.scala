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

package cron4s.expr
import cron4s.CronField
import cats.syntax.all._

/**
  * Representation of a valid CRON expression as an AST
  *
  * @author Antonio Alonso Dominguez
  */
final case class CronExpr(
    seconds: SecondsNode,
    minutes: MinutesNode,
    hours: HoursNode,
    daysOfMonth: DaysOfMonthNode,
    months: MonthsNode,
    daysOfWeek: DaysOfWeekNode
) {
  private[cron4s] lazy val raw: RawCronExpr =
    (seconds, minutes, hours, daysOfMonth, months, daysOfWeek)

  /**
    * Time part of the CRON expression
    */
  lazy val timePart: TimeCronExpr = TimeCronExpr(seconds, minutes, hours)

  /**
    * Date part of the CRON expression
    */
  lazy val datePart: DateCronExpr =
    DateCronExpr(daysOfMonth, months, daysOfWeek)

  override lazy val toString: String =
    raw match {
      case (sec, min, hs, d, m, dw) =>
        List(
          _root_.cron4s.expr.ops.show(sec),
          _root_.cron4s.expr.ops.show(min),
          _root_.cron4s.expr.ops.show(hs),
          _root_.cron4s.expr.ops.show(d),
          _root_.cron4s.expr.ops.show(m),
          _root_.cron4s.expr.ops.show(dw)
        ).mkString(" ")
    }
}

object CronExpr extends Cron4sInstances
