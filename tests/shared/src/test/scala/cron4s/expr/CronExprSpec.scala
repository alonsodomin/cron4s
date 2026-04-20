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

import cats.syntax.show._

import cron4s._

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/**
  * Created by alonsodomin on 07/08/2016.
  */
class CronExprSpec extends AnyFlatSpec with Matchers {
  import CronField._

  val secondExpr: SecondsNode         = ConstNode[Second](15)
  val minuteExpr: MinutesNode         = ConstNode[Minute](10)
  val hourExpr: HoursNode             = ConstNode[Hour](4)
  val dayOfMonthExpr: DaysOfMonthNode = ConstNode[DayOfMonth](12)
  val monthExpr: MonthsNode           = ConstNode[Month](6)
  val dayOfWeekExpr: DaysOfWeekNode   = AnyNode[DayOfWeek]

  val timePart = TimeCronExpr(secondExpr, minuteExpr, hourExpr)
  val datePart = DateCronExpr(dayOfMonthExpr, monthExpr, dayOfWeekExpr)
  val expr = CronExpr(secondExpr, minuteExpr, hourExpr, dayOfMonthExpr, monthExpr, dayOfWeekExpr)

  "field" should "return the expression for the correct cron field" in {
    expr.field[Second] shouldBe expr.seconds
    expr.seconds shouldBe secondExpr

    expr.field[Minute] shouldBe expr.minutes
    expr.minutes shouldBe minuteExpr

    expr.field[Hour] shouldBe expr.hours
    expr.hours shouldBe hourExpr

    expr.field[DayOfMonth] shouldBe expr.daysOfMonth
    expr.daysOfMonth shouldBe dayOfMonthExpr

    expr.field[Month] shouldBe expr.months
    expr.months shouldBe monthExpr

    expr.field[DayOfWeek] shouldBe expr.daysOfWeek
    expr.daysOfWeek shouldBe dayOfWeekExpr

    expr.toString shouldBe expr.show
    expr.show shouldBe "15 10 4 12 6 ?"
  }

  "timePart" should "return the time relative part of the expression" in {
    expr.timePart shouldBe timePart

    timePart.seconds shouldBe secondExpr
    timePart.minutes shouldBe minuteExpr
    timePart.hours shouldBe hourExpr

    timePart.toString shouldBe "15 10 4"
  }

  "datePart" should "return the date relative part of the expression" in {
    expr.datePart shouldBe datePart

    datePart.daysOfMonth shouldBe dayOfMonthExpr
    datePart.months shouldBe monthExpr
    datePart.daysOfWeek shouldBe dayOfWeekExpr

    datePart.toString shouldBe "12 6 ?"
  }
}
