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

import cron4s.{CronField, CronUnit, InvalidField}
import cron4s.expr.ConstNode
import cron4s.base.Enumerated
import cron4s.testkit.Cron4sPropSpec

/**
  * Created by alonsodomin on 29/12/2016.
  */
class ConstNodeValidatorSpec extends Cron4sPropSpec with ValidatorPropSpec {

  import CronField._

  private[this] def check[F <: CronField](implicit unit: CronUnit[F],
                                          ev: Enumerated[CronUnit[F]]): Unit = {
    property(
      s"ConstNode[${unit.field}] with value within range should pass validation") {
      forAll(constGen[F]) { node =>
        NodeValidator[ConstNode[F]].validate(node) shouldBe List
          .empty[InvalidField]
      }
    }

    property(
      s"ConstNode[${unit.field}] with value outside range should return a field error") {
      forAll(invalidConstGen[F]) { node =>
        val expectedError = InvalidField(
          unit.field,
          s"Value ${node.value} is out of bounds for field: ${unit.field}"
        )
        NodeValidator[ConstNode[F]].validate(node) shouldBe List(expectedError)
      }
    }
  }

  check[Second]
  check[Minute]
  check[Hour]
  check[DayOfMonth]
  check[Month]
  check[DayOfWeek]

}
