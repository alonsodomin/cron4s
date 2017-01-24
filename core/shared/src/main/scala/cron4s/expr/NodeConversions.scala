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

  /*implicit def toFieldNode[F <: CronField](ast: RawFieldExpr[F]): FieldNode[F] =
    new FieldNode[F](ast)*/

  implicit def each2Field[F <: CronField](node: EachNode[F]): FieldExpr[F] =
    new FieldExpr(Coproduct[RawFieldExpr[F]](node))

  implicit def const2Field[F <: CronField](node: ConstNode[F]): FieldExpr[F] =
    new FieldExpr(Coproduct[RawFieldExpr[F]](node))

  implicit def between2Field[F <: CronField](node: BetweenNode[F]): FieldExpr[F] =
    new FieldExpr(Coproduct[RawFieldExpr[F]](node))

  implicit def several2Field[F <: CronField](node: SeveralNode[F]): FieldExpr[F] =
    new FieldExpr(Coproduct[RawFieldExpr[F]](node))

  implicit def every2Field[F <: CronField](node: EveryNode[F]): FieldExpr[F] =
    new FieldExpr(Coproduct[RawFieldExpr[F]](node))

  implicit def const2Enumerable[F <: CronField](node: ConstNode[F]): EnumerableExpr[F] =
    new EnumerableExpr(Coproduct[RawEnumerableExpr[F]](node))

  implicit def between2Enumerable[F <: CronField](node: BetweenNode[F]): EnumerableExpr[F] =
    new EnumerableExpr(Coproduct[RawEnumerableExpr[F]](node))

  implicit def each2Divisible[F <: CronField](node: EachNode[F]): DivisibleExpr[F] =
    new DivisibleExpr(Coproduct[RawDivisibleExpr[F]](node))

  implicit def between2Divisible[F <: CronField](node: BetweenNode[F]): DivisibleExpr[F] =
    new DivisibleExpr(Coproduct[RawDivisibleExpr[F]](node))

  implicit def several2Divisible[F <: CronField](node: SeveralNode[F]): DivisibleExpr[F] =
    new DivisibleExpr(Coproduct[RawDivisibleExpr[F]](node))
}
