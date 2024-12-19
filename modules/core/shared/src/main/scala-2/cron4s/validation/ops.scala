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
import cron4s.expr._

import shapeless._
import cron4s.InvalidField

/**
  * Created by alonsodomin on 03/02/2017.
  */
private[validation] object ops {
  object validate extends Poly1 {
    implicit def caseEach[F <: CronField](implicit
        validator: NodeValidator[EachNode[F]]
    ): Case.Aux[EachNode[F], List[InvalidField]] = at[EachNode[F]](validator.validate)

    implicit def caseAny[F <: CronField](implicit
        validator: NodeValidator[AnyNode[F]]
    ): Case.Aux[AnyNode[F], List[InvalidField]] = at[AnyNode[F]](validator.validate)

    implicit def caseConst[F <: CronField](implicit
        validator: NodeValidator[ConstNode[F]]
    ): Case.Aux[ConstNode[F], List[InvalidField]] = at[ConstNode[F]](validator.validate)

    implicit def caseBetween[F <: CronField](implicit
        validator: NodeValidator[BetweenNode[F]]
    ): Case.Aux[BetweenNode[F], List[InvalidField]] = at[BetweenNode[F]](validator.validate)

    implicit def caseSeveral[F <: CronField](implicit
        validator: NodeValidator[SeveralNode[F]]
    ): Case.Aux[SeveralNode[F], List[InvalidField]] = at[SeveralNode[F]](validator.validate)

    implicit def caseEvery[F <: CronField](implicit
        validator: NodeValidator[EveryNode[F]]
    ): Case.Aux[EveryNode[F], List[InvalidField]] = at[EveryNode[F]](validator.validate)

    implicit def caseField[F <: CronField](implicit
        validator: NodeValidator[FieldNode[F]]
    ): Case.Aux[FieldNode[F], List[InvalidField]] = at[FieldNode[F]](validator.validate)

    implicit def caseFieldWithAny[F <: CronField](implicit
        validator: NodeValidator[FieldNodeWithAny[F]]
    ): Case.Aux[FieldNodeWithAny[F], List[InvalidField]] =
      at[FieldNodeWithAny[F]](validator.validate)
  }
}
