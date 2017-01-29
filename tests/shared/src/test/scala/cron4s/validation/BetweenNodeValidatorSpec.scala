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

import cron4s.{CronField, CronUnit, FieldError}
import cron4s.expr.{BetweenNode, ConstNode}
import cron4s.testkit.gen.NodeGenerators
import cron4s.types.Enumerated

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

/**
  * Created by alonsodomin on 29/12/2016.
  */
class BetweenNodeValidatorSpec extends PropSpec
  with GeneratorDrivenPropertyChecks
  with Matchers
  with NodeGenerators {

  import CronField._

  private[this] def check[F <: CronField](implicit unit: CronUnit[F], ev: Enumerated[CronUnit[F]]): Unit = {
    property(s"BetweenNode[${unit.field}] with valid components should pass validation") {
      forAll(betweenGen[F]) { node =>
        NodeValidator[BetweenNode[F]].validate(node) shouldBe List.empty[FieldError]
      }
    }

    property(s"BetweenNode[${unit.field}] with invalid components return the accumulated errors of its components") {
      forAll(invalidBetweenGen[F]) { node =>
        val constErrors = {
          val beginErrors = NodeValidator[ConstNode[F]].validate(node.begin)
          val endErrors = NodeValidator[ConstNode[F]].validate(node.end)

          beginErrors ::: endErrors
        }
        val rangeErrors = {
          if (node.begin.value >= node.end.value) List(FieldError(
            unit.field,
            s"${node.begin.value} should be less than ${node.end.value}"
          ))
          else List.empty[FieldError]
        }
        val expectedErrors = constErrors ::: rangeErrors

        NodeValidator[BetweenNode[F]].validate(node) shouldBe expectedErrors
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
