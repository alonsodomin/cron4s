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

package cron4s
package expr

import cron4s.base.Step
import cron4s.datetime.IsDateTime

sealed trait Constraint[F <: CronField] {
  def step[DT: IsDateTime](from: DT, step: Step): Either[StepError, (Int, DT)]
}

case class EachConstraint[F <: CronField](unit: CronUnit[F]) extends Constraint[F] {
  def step[DT](from: DT, step: Step)(
      implicit DT: IsDateTime[DT]
  ): Either[StepError, (Int, DT)] =
    if (!DT.supportedFields(from).contains(unit.field)) Left(UnsupportedField(unit.field))
    else {
      for {
        currValue <- DT.get(from, unit.field)

      } yield ()
      ???
    }
}
