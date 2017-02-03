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
import shapeless._

import scalaz.Show

/**
  * Created by alonsodomin on 17/12/2016.
  */
private[cron4s] object ops {

  object matches extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachNode[F]](_.matches)
    implicit def caseConst[F <: CronField]   = at[ConstNode[F]](_.matches)
    implicit def caseBetween[F <: CronField] = at[BetweenNode[F]](_.matches)
    implicit def caseSeveral[F <: CronField] = at[SeveralNode[F]](_.matches)
    implicit def caseEvery[F <: CronField]   = at[EveryNode[F]](_.matches)

    implicit def caseField[F <: CronField]      = at[FieldNode[F]](_.raw.fold(matches))
    implicit def caseEnumerable[F <: CronField] = at[EnumerableNode[F]](_.raw.fold(matches))
    implicit def caseDivisible[F <: CronField]  = at[DivisibleNode[F]](_.raw.fold(matches))
  }

  object range extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachNode[F]](_.range)
    implicit def caseConst[F <: CronField]   = at[ConstNode[F]](_.range)
    implicit def caseBetween[F <: CronField] = at[BetweenNode[F]](_.range)
    implicit def caseSeveral[F <: CronField] = at[SeveralNode[F]](_.range)
    implicit def caseEvery[F <: CronField]   = at[EveryNode[F]](_.range)

    implicit def caseField[F <: CronField]      = at[FieldNode[F]](_.raw.fold(range))
    implicit def caseEnumerable[F <: CronField] = at[EnumerableNode[F]](_.raw.fold(range))
    implicit def caseDivisible[F <: CronField]  = at[DivisibleNode[F]](_.raw.fold(range))
  }

  object show extends Poly1 {
    implicit def caseEach[F <: CronField](implicit show: Show[EachNode[F]])
      = at[EachNode[F]](show.shows)
    implicit def caseConst[F <: CronField](implicit show: Show[ConstNode[F]])
      = at[ConstNode[F]](show.shows)
    implicit def caseBetween[F <: CronField](implicit show: Show[BetweenNode[F]])
      = at[BetweenNode[F]](show.shows)
    implicit def caseSeveral[F <: CronField](implicit show: Show[SeveralNode[F]])
      = at[SeveralNode[F]](show.shows)
    implicit def caseEvery[F <: CronField](implicit show: Show[EveryNode[F]])
      = at[EveryNode[F]](show.shows)

    implicit def caseField[F <: CronField](implicit show: Show[FieldNode[F]])      = at[FieldNode[F]](show.shows)
    implicit def caseEnumerable[F <: CronField](implicit show: Show[EnumerableNode[F]]) = at[EnumerableNode[F]](show.shows)
    implicit def caseDivisble[F <: CronField](implicit show: Show[DivisibleNode[F]])   = at[DivisibleNode[F]](show.shows)
  }

  object unit extends Poly1 {
    implicit def caseEach[F <: CronField]    = at[EachNode[F]](_.unit)
    implicit def caseConst[F <: CronField]   = at[ConstNode[F]](_.unit)
    implicit def caseBetween[F <: CronField] = at[BetweenNode[F]](_.unit)
    implicit def caseSeveral[F <: CronField] = at[SeveralNode[F]](_.unit)
    implicit def caseEvery[F <: CronField]   = at[EveryNode[F]](_.unit)

    implicit def caseField[F <: CronField]      = at[FieldNode[F]](_.raw.fold(unit))
    implicit def caseEnumerable[F <: CronField] = at[EnumerableNode[F]](_.raw.fold(unit))
    implicit def caseDivisible[F <: CronField]  = at[DivisibleNode[F]](_.raw.fold(unit))
  }



}
