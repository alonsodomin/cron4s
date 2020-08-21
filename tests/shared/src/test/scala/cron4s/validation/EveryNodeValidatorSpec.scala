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

import cron4s.{CronField, CronUnit}
import cron4s.expr._
import cron4s.base.Enumerated
import cron4s.testkit.Cron4sPropSpec

/**
  * Created by alonsodomin on 30/12/2016.
  */
class EveryNodeValidatorSpec extends Cron4sPropSpec with ValidatorPropSpec {
  import CronField._

  private[this] def check[F <: CronField](implicit
      unit: CronUnit[F],
      enum: Enumerated[CronUnit[F]]
  ): Unit =
    property(s"EveryNode[${unit.field}] with invalid base returns the invalid errors of its base") {
      forAll(everyGen[F]) { node =>
        val expectedErrors = NodeValidator[DivisibleNode[F]].validate(node.base)
        val returnedErrors = NodeValidator[EveryNode[F]].validate(node)

        returnedErrors should contain allElementsOf expectedErrors
      }
    }

  check[Second]
  check[Minute]
  check[Hour]
  check[DayOfMonth]
  check[Month]
  check[DayOfWeek]
}
