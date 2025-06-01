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

import cron4s.CronField
import cron4s.CronUnit
import cron4s.expr._
import cron4s.base.Enumerated
import cron4s.InvalidField

/**
  * Created by alonsodomin on 03/02/2017.
  */
private[validation] object ops extends NodeValidatorInstances {
  type Validatable[F <: CronField] = EachNode[F] | AnyNode[F] | ConstNode[F] | BetweenNode[F] |
    SeveralNode[
      F
    ] | EveryNode[F] | FieldNode[F] | FieldNodeWithAny[F] | Nothing
  def validate[F <: CronField](
      node: Validatable[F]
  )(using Enumerated[CronUnit[F]]): List[InvalidField] = node match {
    case field: EachNode[F]         => summon[NodeValidator[EachNode[F]]].validate(field)
    case field: AnyNode[F]          => summon[NodeValidator[AnyNode[F]]].validate(field)
    case field: ConstNode[F]        => summon[NodeValidator[ConstNode[F]]].validate(field)
    case field: BetweenNode[F]      => summon[NodeValidator[BetweenNode[F]]].validate(field)
    case field: SeveralNode[F]      => implicitly[NodeValidator[SeveralNode[F]]].validate(field)
    case field: EveryNode[F]        => implicitly[NodeValidator[EveryNode[F]]].validate(field)
    case field: FieldNode[F]        => implicitly[NodeValidator[FieldNode[F]]].validate(field)
    case field: FieldNodeWithAny[F] =>
      implicitly[NodeValidator[FieldNodeWithAny[F]]].validate(field)
  }
}
