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

import cats._
import cats.implicits._

import shapeless._

final case class DateCronExpr(
    daysOfMonth: DaysOfMonthNode,
    months: MonthsNode,
    daysOfWeek: DaysOfWeekNode
) {

  private[cron4s] lazy val raw: RawDateCronExpr = Generic[DateCronExpr].to(this)

  override lazy val toString: String =
    raw.map(ops.show).toList.mkString(" ")

}

object DateCronExpr {

  implicit val dateCronEq: Eq[DateCronExpr] = Eq.instance { (lhs, rhs) =>
    lhs.daysOfMonth === rhs.daysOfMonth &&
    lhs.months === rhs.months &&
    lhs.daysOfWeek === rhs.daysOfWeek
  }

  implicit val dateCronShow: Show[DateCronExpr] =
    Show.fromToString[DateCronExpr]

}

final case class TimeCronExpr(
    seconds: SecondsNode,
    minutes: MinutesNode,
    hours: HoursNode
) {

  private[cron4s] lazy val raw: RawTimeCronExpr = Generic[TimeCronExpr].to(this)

  override lazy val toString: String =
    raw.map(ops.show).toList.mkString(" ")

}

object TimeCronExpr {

  implicit val timeCronEq: Eq[TimeCronExpr] = Eq.instance { (lhs, rhs) =>
    lhs.seconds === rhs.seconds &&
    lhs.minutes === rhs.minutes &&
    lhs.hours === rhs.hours
  }

  implicit val timeCronShow: Show[TimeCronExpr] =
    Show.fromToString[TimeCronExpr]

}
