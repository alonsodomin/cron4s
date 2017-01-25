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

import cron4s.{CronField, CronUnit, generic}

import shapeless._

import scalaz.Show

/**
  * Representation of a valid CRON expression as an AST
  *
  * @author Antonio Alonso Dominguez
  */
object CronExpr {

  implicit val CronExprShow: Show[CronExpr] = Show.shows { expr =>
    expr.raw.map(generic.ops.show).toList.mkString(" ")
  }

}

final case class CronExpr(
    seconds: SecondsNode,
    minutes: MinutesNode,
    hours: HoursNode,
    daysOfMonth: DaysOfMonthNode,
    months: MonthsNode,
    daysOfWeek: DaysOfWeekNode) {

  private[cron4s] lazy val raw: RawCronExpr = Generic[CronExpr].to(this)

  /**
    * Time part of the CRON expression
    */
  lazy val timePart: TimeCronExpr = TimeCronExpr(seconds, minutes, hours)

  /**
    * Date part of the CRON expression
    */
  lazy val datePart: DateCronExpr = DateCronExpr(daysOfMonth, months, daysOfWeek)

  /**
    * Generic field accessor. Given a CronField, this method can be used
    * to access the expression on that given field.
    *
    * @param unit the CronUnit for the given field
    * @tparam F CronField type
    * @return field-based expression for given field
    */
  def field[F <: CronField](implicit unit: CronUnit[F]): RawFieldNode[F] = unit.field match {
    case CronField.Second     => seconds.asInstanceOf[RawFieldNode[F]]
    case CronField.Minute     => minutes.asInstanceOf[RawFieldNode[F]]
    case CronField.Hour       => hours.asInstanceOf[RawFieldNode[F]]
    case CronField.DayOfMonth => daysOfMonth.asInstanceOf[RawFieldNode[F]]
    case CronField.Month      => months.asInstanceOf[RawFieldNode[F]]
    case CronField.DayOfWeek  => daysOfWeek.asInstanceOf[RawFieldNode[F]]
  }

  def ranges: List[IndexedSeq[Int]] = raw.map(generic.ops.range).toList

  override def toString: String = Show[CronExpr].shows(this)

}
