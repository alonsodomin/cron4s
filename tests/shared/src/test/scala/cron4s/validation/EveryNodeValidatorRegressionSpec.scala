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

package cron4s.validation

import cats.syntax.show._

import cron4s._
import cron4s.expr.{EachNode, EveryNode}

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/** Created by alonsodomin on 31/12/2016.
  */
class EveryNodeValidatorRegressionSpec extends AnyFlatSpec with Matchers {
  import CronField._

  "An Every node" should "pass validation if it's evenly divided" in {
    val eachNode  = EachNode[Second]
    val everyNode = EveryNode[Second](eachNode, 12)

    val returnedErrors = NodeValidator[EveryNode[Second]].validate(everyNode)
    returnedErrors shouldBe List.empty[InvalidField]
  }

  it should "not pass validation if it is not evenly divided" in {
    val eachNode  = EachNode[Second]
    val everyNode = EveryNode[Second](eachNode, 13)

    val expectedError = InvalidField(
      Second,
      s"Step '${everyNode.freq}' does not evenly divide the value '${everyNode.base.show}'"
    )

    val returnedErrors = NodeValidator[EveryNode[Second]].validate(everyNode)
    returnedErrors shouldBe List(expectedError)
  }
}
