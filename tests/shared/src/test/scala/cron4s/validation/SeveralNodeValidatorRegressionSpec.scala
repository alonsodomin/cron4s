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
import cron4s.expr.{BetweenNode, ConstNode, SeveralNode}

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

    val returnedErrors = NodeValidator[SeveralNode[Minute]].validate(severalNode)

    returnedErrors shouldBe List(InvalidField(
      Minute,
      s"Value '${node1.show}' at field Minute is implied by '${node2.show}'"
    ))
  }

  it should "not fail if they overlap without full implication" in {
    val node1 = BetweenNode[Minute](ConstNode(6), ConstNode(12))
    val node2 = BetweenNode[Minute](ConstNode(10), ConstNode(24))
    val severalNode = SeveralNode[Minute](node1, node2)

    val returnedErrors = NodeValidator[SeveralNode[Minute]].validate(severalNode)
    returnedErrors shouldBe List.empty[InvalidField]
  }

}
