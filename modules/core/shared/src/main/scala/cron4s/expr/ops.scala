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

import cron4s.CronField

import shapeless._
import cron4s.base.Predicate
import cron4s.CronUnit
/**
  * Created by alonsodomin on 17/12/2016.
  */
private[cron4s] object ops {
  object matches extends Poly1 {
    implicit def caseEach[F <: CronField]: Case.Aux[EachNode[F], Predicate[Int]] =
      at[EachNode[F]](_.matches)
    implicit def caseAny[F <: CronField]: Case.Aux[AnyNode[F], Predicate[Int]] =
      at[AnyNode[F]](_.matches)
    implicit def caseConst[F <: CronField]: Case.Aux[ConstNode[F], Predicate[Int]] =
      at[ConstNode[F]](_.matches)
    implicit def caseBetween[F <: CronField]: Case.Aux[BetweenNode[F], Predicate[Int]] =
      at[BetweenNode[F]](_.matches)
    implicit def caseSeveral[F <: CronField]: Case.Aux[SeveralNode[F], Predicate[Int]] =
      at[SeveralNode[F]](_.matches)
    implicit def caseEvery[F <: CronField]: Case.Aux[EveryNode[F], Predicate[Int]] =
      at[EveryNode[F]](_.matches)

    implicit def caseField[F <: CronField]: Case.Aux[FieldNode[F], Predicate[Int]] =
      at[FieldNode[F]](_.raw.fold(matches))
    implicit def caseFieldWithAny[F <: CronField]: Case.Aux[FieldNodeWithAny[F], Predicate[Int]] =
      at[FieldNodeWithAny[F]](_.raw.fold(matches))
    implicit def caseEnumerable[F <: CronField]: Case.Aux[EnumerableNode[F], Predicate[Int]] =
      at[EnumerableNode[F]](_.raw.fold(matches))
    implicit def caseDivisible[F <: CronField]: Case.Aux[DivisibleNode[F], Predicate[Int]] =
      at[DivisibleNode[F]](_.raw.fold(matches))
  }

  object range extends Poly1 {
    implicit def caseEach[F <: CronField]: Case.Aux[EachNode[F], IndexedSeq[Int]] =
      at[EachNode[F]](_.range)
    implicit def caseAny[F <: CronField]: Case.Aux[AnyNode[F], IndexedSeq[Int]] =
      at[AnyNode[F]](_.range)
    implicit def caseConst[F <: CronField]: Case.Aux[ConstNode[F], IndexedSeq[Int]] =
      at[ConstNode[F]](_.range)
    implicit def caseBetween[F <: CronField]: Case.Aux[BetweenNode[F], IndexedSeq[Int]] =
      at[BetweenNode[F]](_.range)
    implicit def caseSeveral[F <: CronField]: Case.Aux[SeveralNode[F], IndexedSeq[Int]] =
      at[SeveralNode[F]](_.range)
    implicit def caseEvery[F <: CronField]: Case.Aux[EveryNode[F], IndexedSeq[Int]] =
      at[EveryNode[F]](_.range)

    implicit def caseField[F <: CronField]: Case.Aux[FieldNode[F], IndexedSeq[Int]] =
      at[FieldNode[F]](_.raw.fold(range))
    implicit def caseFieldWithAny[F <: CronField]: Case.Aux[FieldNodeWithAny[F], IndexedSeq[Int]] =
      at[FieldNodeWithAny[F]](_.raw.fold(range))
    implicit def caseEnumerable[F <: CronField]: Case.Aux[EnumerableNode[F], IndexedSeq[Int]] =
      at[EnumerableNode[F]](_.raw.fold(range))
    implicit def caseDivisible[F <: CronField]: Case.Aux[DivisibleNode[F], IndexedSeq[Int]] =
      at[DivisibleNode[F]](_.raw.fold(range))
  }

  object show extends Poly1 {
    implicit def caseEach[F <: CronField](implicit
        show: Show[EachNode[F]]
    ): Case.Aux[EachNode[F], String] =
      at[EachNode[F]](show.show)
    implicit def caseAny[F <: CronField](implicit
        show: Show[AnyNode[F]]
    ): Case.Aux[AnyNode[F], String] =
      at[AnyNode[F]](show.show)
    implicit def caseConst[F <: CronField](implicit
        show: Show[ConstNode[F]]
    ): Case.Aux[ConstNode[F], String] =
      at[ConstNode[F]](show.show)
    implicit def caseBetween[F <: CronField](implicit
        show: Show[BetweenNode[F]]
    ): Case.Aux[BetweenNode[F], String] =
      at[BetweenNode[F]](show.show)
    implicit def caseSeveral[F <: CronField](implicit
        show: Show[SeveralNode[F]]
    ): Case.Aux[SeveralNode[F], String] =
      at[SeveralNode[F]](show.show)
    implicit def caseEvery[F <: CronField](implicit
        show: Show[EveryNode[F]]
    ): Case.Aux[EveryNode[F], String] =
      at[EveryNode[F]](show.show)

    implicit def caseField[F <: CronField](implicit
        show: Show[FieldNode[F]]
    ): Case.Aux[FieldNode[F], String] =
      at[FieldNode[F]](show.show)
    implicit def caseFieldWithAny[F <: CronField](implicit
        show: Show[FieldNodeWithAny[F]]
    ): Case.Aux[FieldNodeWithAny[F], String] =
      at[FieldNodeWithAny[F]](show.show)
    implicit def caseEnumerable[F <: CronField](implicit
        show: Show[EnumerableNode[F]]
    ): Case.Aux[EnumerableNode[F], String] =
      at[EnumerableNode[F]](show.show)
    implicit def caseDivisble[F <: CronField](implicit
        show: Show[DivisibleNode[F]]
    ): Case.Aux[DivisibleNode[F], String] =
      at[DivisibleNode[F]](show.show)
  }

  object unit extends Poly1 {
    implicit def caseEach[F <: CronField]: Case.Aux[EachNode[F], CronUnit[F]] =
      at[EachNode[F]](_.unit)
    implicit def caseAny[F <: CronField]: Case.Aux[AnyNode[F], CronUnit[F]] = at[AnyNode[F]](_.unit)
    implicit def caseConst[F <: CronField]: Case.Aux[ConstNode[F], CronUnit[F]] =
      at[ConstNode[F]](_.unit)
    implicit def caseBetween[F <: CronField]: Case.Aux[BetweenNode[F], CronUnit[F]] =
      at[BetweenNode[F]](_.unit)
    implicit def caseSeveral[F <: CronField]: Case.Aux[SeveralNode[F], CronUnit[F]] =
      at[SeveralNode[F]](_.unit)
    implicit def caseEvery[F <: CronField]: Case.Aux[EveryNode[F], CronUnit[F]] =
      at[EveryNode[F]](_.unit)

    implicit def caseField[F <: CronField]: Case.Aux[FieldNode[F], CronUnit[F]] =
      at[FieldNode[F]](_.raw.fold(unit))
    implicit def caseFieldWithAny[F <: CronField]: Case.Aux[FieldNodeWithAny[F], CronUnit[F]] =
      at[FieldNodeWithAny[F]](_.raw.fold(unit))
    implicit def caseEnumerable[F <: CronField]: Case.Aux[EnumerableNode[F], CronUnit[F]] =
      at[EnumerableNode[F]](_.raw.fold(unit))
    implicit def caseDivisible[F <: CronField]: Case.Aux[DivisibleNode[F], CronUnit[F]] =
      at[DivisibleNode[F]](_.raw.fold(unit))
  }
}
