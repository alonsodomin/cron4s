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

import cron4s.CronField
import cron4s.expr._
import cron4s.testkit.Cron4sPropSpec

import org.scalatest.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

/**
  * Created by alonsodomin on 07/08/2016.
  */
class StepperSpec extends Cron4sPropSpec with TableDrivenPropertyChecks with Matchers {
  import CronField._
  import testdummy._

  val onlyTuesdaysAt12 = CronExpr(
    ConstNode[Second](0),
    ConstNode[Minute](0),
    ConstNode[Hour](12),
    EachNode[DayOfMonth],
    EachNode[Month],
    ConstNode[DayOfWeek](1)
  )
  val everyMinuteBetween2And3 = CronExpr(
    ConstNode[Second](0),
    EachNode[Minute],
    BetweenNode(ConstNode[Hour](2), ConstNode[Hour](3)),
    EachNode[DayOfMonth],
    EachNode[Month],
    EachNode[DayOfWeek]
  )

  val sample = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, DummyDateTime(0, 0, 0, 1, 8, 0), 1, DummyDateTime(0, 0, 12, 1, 8, 1)),
    (everyMinuteBetween2And3, DummyDateTime(0, 0, 2, 1, 1, 1), 1, DummyDateTime(0, 1, 2, 1, 1, 1)),
    (everyMinuteBetween2And3, DummyDateTime(0, 59, 2, 1, 1, 1), 1, DummyDateTime(0, 0, 3, 1, 1, 1))
  )

  property("step") {
    forAll(sample) { (expr: CronExpr, from: DummyDateTime, stepSize: Int, expected: DummyDateTime) =>
      expr.step(from, stepSize) shouldBe Some(expected)
    }
  }

}
