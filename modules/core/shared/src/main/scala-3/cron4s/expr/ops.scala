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

import cats.Show
import cats.implicits.toShow
import cron4s.CronField

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
  def matches[F <: CronField](field: PolyField[F]): Predicate[Int] = ???
  def range[F <: CronField](field: PolyField[F]): IndexedSeq[Int]  = ???
  def show[F <: CronField](field: PolyField[F]): String = field match {
    case n: EachNode[F]         => n.show
    case n: AnyNode[F]          => n.show
    case n: ConstNode[F]        => n.show
    case n: BetweenNode[F]      => n.show
    case n: SeveralNode[F]      => n.show
    case n: EveryNode[F]        => n.show
    case n: FieldNode[F]        => ???
    case n: FieldNodeWithAny[F] => ???
    case n: EnumerableNode[F]   => ???
    case n: DivisibleNode[F]    => ???
  }
  def unit[F <: CronField](field: PolyField[F]): CronUnit[F] = field match {
    case n: EachNode[F]         => n.unit
    case n: AnyNode[F]          => n.unit
    case n: ConstNode[F]        => n.unit
    case n: BetweenNode[F]      => n.unit
    case n: SeveralNode[F]      => n.unit
    case n: EveryNode[F]        => n.unit
    case n: FieldNode[F]        => ???
    case n: FieldNodeWithAny[F] => ???
    case n: EnumerableNode[F]   => ???
    case n: DivisibleNode[F]    => ???
  }
}
