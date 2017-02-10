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

package cron4s.expr

import cron4s.CronField

import shapeless.Coproduct

import scala.language.implicitConversions

/**
  * Created by alonsodomin on 28/12/2016.
  */
private[cron4s] trait NodeConversions {

  implicit def each2Field[F <: CronField](node: EachNode[F]): FieldNode[F] =
    new FieldNode(Coproduct[RawFieldNode[F]](node))

  implicit def const2Field[F <: CronField](node: ConstNode[F]): FieldNode[F] =
    new FieldNode(Coproduct[RawFieldNode[F]](node))

  implicit def between2Field[F <: CronField](node: BetweenNode[F]): FieldNode[F] =
    new FieldNode(Coproduct[RawFieldNode[F]](node))

  implicit def several2Field[F <: CronField](node: SeveralNode[F]): FieldNode[F] =
    new FieldNode(Coproduct[RawFieldNode[F]](node))

  implicit def every2Field[F <: CronField](node: EveryNode[F]): FieldNode[F] =
    new FieldNode(Coproduct[RawFieldNode[F]](node))

  implicit def any2FieldWithAny[F <: CronField](node: AnyNode[F]): FieldNodeWithAny[F] =
    new FieldNodeWithAny(Coproduct[RawFieldNodeWithAny[F]](node))

  implicit def each2FieldWithAny[F <: CronField](node: EachNode[F]): FieldNodeWithAny[F] =
    new FieldNodeWithAny(Coproduct[RawFieldNodeWithAny[F]](node))

  implicit def const2FieldWithAny[F <: CronField](node: ConstNode[F]): FieldNodeWithAny[F] =
    new FieldNodeWithAny(Coproduct[RawFieldNodeWithAny[F]](node))

  implicit def between2FieldWithAny[F <: CronField](node: BetweenNode[F]): FieldNodeWithAny[F] =
    new FieldNodeWithAny(Coproduct[RawFieldNodeWithAny[F]](node))

  implicit def several2FieldWithAny[F <: CronField](node: SeveralNode[F]): FieldNodeWithAny[F] =
    new FieldNodeWithAny(Coproduct[RawFieldNodeWithAny[F]](node))

  implicit def every2FieldWithAny[F <: CronField](node: EveryNode[F]): FieldNodeWithAny[F] =
    new FieldNodeWithAny(Coproduct[RawFieldNodeWithAny[F]](node))

  implicit def const2Enumerable[F <: CronField](node: ConstNode[F]): EnumerableNode[F] =
    new EnumerableNode(Coproduct[RawEnumerableNode[F]](node))

  implicit def between2Enumerable[F <: CronField](node: BetweenNode[F]): EnumerableNode[F] =
    new EnumerableNode(Coproduct[RawEnumerableNode[F]](node))

  implicit def each2Divisible[F <: CronField](node: EachNode[F]): DivisibleNode[F] =
    new DivisibleNode(Coproduct[RawDivisibleNode[F]](node))

  implicit def between2Divisible[F <: CronField](node: BetweenNode[F]): DivisibleNode[F] =
    new DivisibleNode(Coproduct[RawDivisibleNode[F]](node))

  implicit def several2Divisible[F <: CronField](node: SeveralNode[F]): DivisibleNode[F] =
    new DivisibleNode(Coproduct[RawDivisibleNode[F]](node))
}
