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

import cron4s.each2Field
import cron4s.CronField
import cron4s.CronField.Year

final case class DateCronExpr(
    daysOfMonth: DaysOfMonthNode,
    months: MonthsNode,
    daysOfWeek: DaysOfWeekNode,
    years: Option[YearsNode] = None
) {
  private[cron4s] lazy val raw: RawDateCronExpr = (
    daysOfMonth,
    months,
    daysOfWeek,
    years.getOrElse[YearsNode](EachNode[Year])
  )

  override lazy val toString: String = {
    val (daysOfMonth, months, daysOfWeek, _) = raw
    years match {
      case Some(years) =>
        List(
          _root_.cron4s.expr.ops.show(daysOfMonth),
          _root_.cron4s.expr.ops.show(months),
          _root_.cron4s.expr.ops.show(daysOfWeek),
          _root_.cron4s.expr.ops.show(years)
        ).mkString(" ")
      case None =>
        List(
          _root_.cron4s.expr.ops.show(daysOfMonth),
          _root_.cron4s.expr.ops.show(months),
          _root_.cron4s.expr.ops.show(daysOfWeek)
        ).mkString(" ")
    }
  }
}

object DateCronExpr extends DateCronExprInstances

final case class TimeCronExpr(
    seconds: SecondsNode,
    minutes: MinutesNode,
    hours: HoursNode
) {
  private[cron4s] lazy val raw: RawTimeCronExpr = (seconds, minutes, hours)

  override lazy val toString: String =
    List(seconds, minutes, hours).map(_root_.cron4s.expr.ops.show).mkString(" ")
}

object TimeCronExpr extends TimeCronExprInstances
