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
import cron4s.expr._

import org.scalatest._

/**
  * Created by alonsodomin on 05/08/2016.
  */
class SeveralNodeValidatorRegressionSpec extends FlatSpec with Matchers {
  import CronField._

  "A series of nodes" should "not be valid if any of them implies the other" in {
    val node1 = ConstNode[Minute](23)
    val node2 = BetweenNode[Minute](ConstNode(10), ConstNode(24))
    val severalNode = SeveralNode[Minute](node1, node2)

    val returnedErrors =
      NodeValidator[SeveralNode[Minute]].validate(severalNode)

    returnedErrors shouldBe List(
      InvalidField(
        Minute,
        s"Value '${node1.show}' is implied by '${node2.show}'"
      ))
  }

  it should "not fail if they overlap without full implication" in {
    val node1 = BetweenNode[Minute](ConstNode(6), ConstNode(12))
    val node2 = BetweenNode[Minute](ConstNode(10), ConstNode(24))
    val severalNode = SeveralNode[Minute](node1, node2)

    val returnedErrors =
      NodeValidator[SeveralNode[Minute]].validate(severalNode)
    returnedErrors shouldBe List.empty[InvalidField]
  }

  it should "include both error messages when implication is bidirectional" in {
    val node1 = ConstNode[Second](10)
    val node2 = ConstNode[Second](10)
    val severalNode = SeveralNode[Second](node1, node2)

    val returnedErrors =
      NodeValidator[SeveralNode[Second]].validate(severalNode)
    returnedErrors shouldBe List(
      InvalidField(Second,
                   s"Value '${node1.show}' is implied by '${node2.show}'"),
      InvalidField(Second,
                   s"Value '${node2.show}' is implied by '${node1.show}'")
    )
  }

  it should "accumulate all the implication errors" in {
    val node1 = ConstNode[Month](5)
    val node2 = BetweenNode[Month](ConstNode(2), ConstNode(6))
    val node3 = BetweenNode[Month](ConstNode(4), ConstNode(8))
    val severalNode = SeveralNode[Month](node1, node2, node3)

    val returnedErrors = NodeValidator[SeveralNode[Month]].validate(severalNode)
    returnedErrors shouldBe List(
      InvalidField(Month,
                   s"Value '${node1.show}' is implied by '${node2.show}'"),
      InvalidField(Month,
                   s"Value '${node1.show}' is implied by '${node3.show}'")
    )
  }

  it should "not check for implication of elements when a subexpression is invalid" in {
    val node1 = ConstNode[Second](23)
    val node2 = BetweenNode[Second](ConstNode(-390), ConstNode(120))
    val severalNode = SeveralNode[Second](node1, node2)

    val returnedErrors =
      NodeValidator[SeveralNode[Second]].validate(severalNode)
    returnedErrors shouldBe List(
      InvalidField(
        Second,
        s"Value ${node2.begin.show} is out of bounds for field: Second"),
      InvalidField(
        Second,
        s"Value ${node2.end.show} is out of bounds for field: Second")
    )
  }

}
