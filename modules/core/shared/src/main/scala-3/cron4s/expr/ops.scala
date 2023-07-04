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
import cats.syntax.all.toShow
import cron4s.base.Predicate
import cron4s.CronUnit

/**
  * Created by alonsodomin on 17/12/2016.
  */
private[cron4s] object ops {
  type PolyField[F <: CronField] =
    EachNode[F] | AnyNode[F] | ConstNode[F] | BetweenNode[F] | SeveralNode[F] | EveryNode[
      F
    ] | FieldNode[F] | FieldNodeWithAny[F] | EnumerableNode[F] | DivisibleNode[F]
  import cron4s.syntax.all.toExprOps
  def matches[F <: CronField](field: PolyField[F]): Predicate[Int] = field match {
    case node: EachNode[F]         => node.matches
    case node: AnyNode[F]          => node.matches
    case node: ConstNode[F]        => node.matches
    case node: BetweenNode[F]      => node.matches
    case node: SeveralNode[F]      => node.matches
    case node: EveryNode[F]        => node.matches
    case node: FieldNode[F]        => matches(node.raw)
    case node: FieldNodeWithAny[F] => matches(node.raw)
    case node: EnumerableNode[F]   => matches(node.raw)
    case node: DivisibleNode[F]    => matches(node.raw)
  }
  def range[F <: CronField](field: PolyField[F]): IndexedSeq[Int] = field match {
    case node: EachNode[F]         => node.range
    case node: AnyNode[F]          => node.range
    case node: ConstNode[F]        => node.range
    case node: BetweenNode[F]      => node.range
    case node: SeveralNode[F]      => node.range
    case node: EveryNode[F]        => node.range
    case node: FieldNode[F]        => range(node.raw)
    case node: FieldNodeWithAny[F] => range(node.raw)
    case node: EnumerableNode[F]   => range(node.raw)
    case node: DivisibleNode[F]    => range(node.raw)
  }
  def show[F <: CronField](field: PolyField[F]): String = field match {
    case n: EachNode[F]         => n.show
    case n: AnyNode[F]          => n.show
    case n: ConstNode[F]        => n.show
    case n: BetweenNode[F]      => n.show
    case n: SeveralNode[F]      => n.show
    case n: EveryNode[F]        => n.show
    case n: FieldNode[F]        => n.show
    case n: FieldNodeWithAny[F] => n.show
    case n: EnumerableNode[F]   => n.show
    case n: DivisibleNode[F]    => n.show
  }
  def unit[F <: CronField](field: PolyField[F]): CronUnit[F] = field match {
    case n: EachNode[F]         => n.unit
    case n: AnyNode[F]          => n.unit
    case n: ConstNode[F]        => n.unit
    case n: BetweenNode[F]      => n.unit
    case n: SeveralNode[F]      => n.unit
    case n: EveryNode[F]        => n.unit
    case n: FieldNode[F]        => unit(n.raw)
    case n: FieldNodeWithAny[F] => unit(n.raw)
    case n: EnumerableNode[F]   => unit(n.raw)
    case n: DivisibleNode[F]    => unit(n.raw)
  }
}
