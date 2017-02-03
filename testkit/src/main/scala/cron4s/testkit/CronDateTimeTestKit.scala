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

import cron4s.CronField._
import cron4s.datetime.DateTimeAdapter
import cron4s.expr._

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
abstract class CronDateTimeTestKit[DateTime: DateTimeAdapter: Equal]
  extends Cron4sPropSpec with TableDrivenPropertyChecks { this: DateTimeTestKitBase[DateTime] =>

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

  lazy val samples = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, createDateTime(0, 0, 0, 1, 8, 0), 1, createDateTime(0, 0, 12, 2, 8, 1)),
    (onlySundays, createDateTime(0, 0, 0, 1, 8, 0), 1, createDateTime(0, 1, 0, 7, 8, 6))
  )

  property("step") {
    forAll(samples) { (expr: CronExpr, initial: DateTime, stepSize: Int, expected: DateTime) =>
      val returnedDateTime = expr.step(initial, stepSize)
      returnedDateTime.map(_ ?== expected).getOrElse(falsified)
    }
  }

}
