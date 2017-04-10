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

package cron4s.testkit

import cats.Eq
import catalysts.Platform

import cron4s.CronField._
import cron4s.datetime.IsDateTime
import cron4s.expr._

import org.scalatest.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

/**
  * Created by alonsodomin on 29/08/2016.
  */
abstract class CronDateTimeTestKit[DateTime: IsDateTime: Eq]
  extends Cron4sPropSpec with Matchers with TableDrivenPropertyChecks { this: DateTimeTestKitBase[DateTime] =>

  val onlyTuesdaysAt12 = CronExpr(
    ConstNode[Second](0),
    ConstNode[Minute](0),
    ConstNode[Hour](12),
    EachNode[DayOfMonth],
    EachNode[Month],
    ConstNode[DayOfWeek](1)
  )
  val onlySundays = CronExpr(
    ConstNode[Second](0),
    EachNode[Minute],
    EachNode[Hour],
    EachNode[DayOfMonth],
    EachNode[Month],
    ConstNode[DayOfWeek](6)
  )

  // https://github.com/alonsodomin/cron4s/issues/59
  val betweenDayOfWeek = CronExpr(
    ConstNode[Second](0),
    ConstNode[Minute](0),
    ConstNode[Hour](0),
    EachNode[DayOfMonth],
    EachNode[Month],
    BetweenNode[DayOfWeek](ConstNode(1), ConstNode(3))
  )

  val betweenMonth = CronExpr(
    ConstNode[Second](0),
    ConstNode[Minute](0),
    ConstNode[Hour](0),
    EachNode[DayOfMonth],
    BetweenNode[Month](ConstNode(4), ConstNode(10)),
    EachNode[DayOfWeek]
  )

  lazy val samples = Table(
    ("expr",           "from",                              "stepSize", "expected"),
    (onlyTuesdaysAt12, createDateTime(0, 0, 0, 1, 8, 2016),          1, createDateTime(0, 0, 12, 2, 8, 2016)),
    (onlySundays,      createDateTime(0, 0, 0, 1, 8, 2016),          1, createDateTime(0, 1, 0, 7, 8, 2016)),
    (betweenDayOfWeek, createDateTime(0, 0, 2, 11, 3, 2016),         1, createDateTime(0, 0, 0, 15, 3, 2016)),
    (betweenDayOfWeek, createDateTime(0, 0, 2, 7, 3, 2016),         -1, createDateTime(0, 0, 0, 3, 3, 2016)),
    (betweenMonth,     createDateTime(0, 1, 1, 4, 11, 2016),         1, createDateTime(0, 0, 0, 5, 4, 2017))
  )

  property("step") {
    forAll(samples) { (expr: CronExpr, initial: DateTime, stepSize: Int, expected: DateTime) =>
      val returnedDateTime = expr.step(initial, stepSize)
      // Workaround ScalaJS bug https://github.com/scala-js/scala-js/pull/2713
      if (Platform.isJvm) returnedDateTime shouldBe Some(expected)
    }
  }

}
