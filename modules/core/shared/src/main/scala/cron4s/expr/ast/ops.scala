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

package cron4s.expr.ast

import cats.Show

import cron4s.CronField
import cron4s.base._
import cron4s.syntax.all._

import shapeless._

/**
  * Created by alonsodomin on 17/12/2016.
  */
private[cron4s] object ops {

  object matches extends Poly1 {
    // implicit def caseEach[F <: CronField]    = at[EachInRange[F]](_.matches)
    // implicit def caseAny[F <: CronField]     = at[AnyInRange[F]](_.matches)
    // implicit def caseConst[F <: CronField]   = at[ConstValue[F]](_.matches)
    // implicit def caseBetween[F <: CronField] = at[BoundedRange[F]](_.matches)
    // implicit def caseSeveral[F <: CronField] = at[EnumeratedRange[F]](_.matches)
    // implicit def caseEvery[F <: CronField]   = at[SteppingRange[F]](_.matches)

    // implicit def caseField[F <: CronField] =
    //   at[RangeNode[F]](_.raw.fold(matches))
    // implicit def caseFieldWithAny[F <: CronField] =
    //   at[FieldNodeWithAny[F]](_.raw.fold(matches))
    // implicit def caseEnumerable[F <: CronField] =
    //   at[EnumerableNode[F]](_.raw.fold(matches))
    // implicit def caseDivisible[F <: CronField] =
    //   at[DivisibleNode[F]](_.raw.fold(matches))
  }

  object unfold extends Poly1 {
    implicit def caseEach[F <: CronField]      = at[EachInRange[F]](_.unfold)
    implicit def caseAny[F <: CronField]       = at[AnyInRange[F]](_.unfold)
    implicit def caseConst[F <: CronField]     = at[ConstValue[F]](_.unfold)
    implicit def caseBounded[F <: CronField]   = at[BoundedRange[F]](_.unfold)
    implicit def caseEnmerated[F <: CronField] = at[EnumeratedRange[F]](_.unfold)
    implicit def caseStepping[F <: CronField]  = at[SteppingRange[F]](_.unfold)
  }

  // object show extends Poly1 {
  //   implicit def caseEach[F <: CronField](implicit show: Show[EachNode[F]]) =
  //     at[EachNode[F]](show.show)
  //   implicit def caseAny[F <: CronField](implicit show: Show[AnyNode[F]]) =
  //     at[AnyNode[F]](show.show)
  //   implicit def caseConst[F <: CronField](implicit show: Show[ConstNode[F]]) =
  //     at[ConstNode[F]](show.show)
  //   implicit def caseBetween[F <: CronField](implicit show: Show[BetweenNode[F]]) =
  //     at[BetweenNode[F]](show.show)
  //   implicit def caseSeveral[F <: CronField](implicit show: Show[SeveralNode[F]]) =
  //     at[SeveralNode[F]](show.show)
  //   implicit def caseEvery[F <: CronField](implicit show: Show[EveryNode[F]]) =
  //     at[EveryNode[F]](show.show)

  //   implicit def caseField[F <: CronField](implicit show: Show[FieldNode[F]]) =
  //     at[FieldNode[F]](show.show)
  //   implicit def caseFieldWithAny[F <: CronField](implicit show: Show[FieldNodeWithAny[F]]) =
  //     at[FieldNodeWithAny[F]](show.show)
  //   implicit def caseEnumerable[F <: CronField](implicit show: Show[EnumerableNode[F]]) =
  //     at[EnumerableNode[F]](show.show)
  //   implicit def caseDivisble[F <: CronField](implicit show: Show[DivisibleNode[F]]) =
  //     at[DivisibleNode[F]](show.show)
  // }

  object unit extends Poly1 {
    implicit def caseEach[F <: CronField]      = at[EachInRange[F]](_.unit)
    implicit def caseAny[F <: CronField]       = at[AnyInRange[F]](_.unit)
    implicit def caseConst[F <: CronField]     = at[ConstValue[F]](_.unit)
    implicit def caseBounded[F <: CronField]   = at[BoundedRange[F]](_.begin.unit)
    implicit def caseEnmerated[F <: CronField] = at[EnumeratedRange[F]](_.unit)
    implicit def caseStepping[F <: CronField]  = at[SteppingRange[F]](_.unit)
  }

}
