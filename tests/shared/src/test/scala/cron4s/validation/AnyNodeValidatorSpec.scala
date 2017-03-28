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
import cron4s.expr.AnyNode
import cron4s.testkit.Cron4sPropSpec
import cron4s.testkit.gen.ArbitratyAnyNode

import org.scalacheck.Arbitrary

/**
  * Created by alonsodomin on 10/02/2017.
  */
class AnyNodeValidatorSpec extends Cron4sPropSpec
  with ValidatorPropSpec
  with ArbitratyAnyNode {

  import CronField._

  private[this] def check[F <: CronField](implicit unit: CronUnit[F], arbNode: Arbitrary[AnyNode[F]]): Unit = {
    property(s"EachNode[${unit.field}] should always pass validation") {
      forAll { (node: AnyNode[F]) =>
        NodeValidator[AnyNode[F]].validate(node) shouldBe List.empty[FieldError]
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
