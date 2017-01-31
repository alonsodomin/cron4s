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
import cron4s.expr.{EachNode, CronExpr}

import org.scalacheck._

import shapeless._

/**
  * Created by alonsodomin on 06/08/2016.
  */
class CronDateTimeSpec extends Properties("CronDateTime") with DummyTestBase {
  import Arbitrary.arbitrary
  import CronField._
  import CronUnit._
  import Prop._
  import testdummy._

  val eachExpr = CronExpr(
    EachNode[Second],
    EachNode[Minute],
    EachNode[Hour],
    EachNode[DayOfMonth],
    EachNode[Month],
    EachNode[DayOfWeek]
  )

  val eachDateCombinations = for {
    expr <- Gen.const(eachExpr)
    dt   <- arbitrary[DummyDateTime]
  } yield (expr, dt)

  property("each expression matches everything") = forAll(eachDateCombinations) {
    case (expr, dt) => expr.allOf(dt) && expr.anyOf(dt)
  }

  property("next is equals to step with 1") = forAll(eachDateCombinations) {
    case (expr, dt) => expr.next(dt) == expr.step(dt, 1)
  }

  property("previous is equals to step with -1") = forAll(eachDateCombinations) {
    case (expr, dt) => expr.prev(dt) == expr.step(dt, -1)
  }

}
