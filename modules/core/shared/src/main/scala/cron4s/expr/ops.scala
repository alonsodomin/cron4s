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

import cats.Show

import cron4s.CronField

import shapeless._

/**
  * Created by alonsodomin on 17/12/2016.
  */
private[cron4s] object ops {

  object show extends Poly1 {
    implicit def caseEach[F <: CronField](implicit show: Show[EachInRange[F]]) =
      at[EachInRange[F]](show.show)
    implicit def caseAny[F <: CronField](implicit show: Show[AnyInRange[F]]) =
      at[AnyInRange[F]](show.show)
    implicit def caseConst[F <: CronField](implicit show: Show[ConstValue[F]]) =
      at[ConstValue[F]](show.show)
    implicit def caseBetween[F <: CronField](implicit show: Show[BoundedRange[F]]) =
      at[BoundedRange[F]](show.show)
    implicit def caseSeveral[F <: CronField](implicit show: Show[EnumeratedRange[F]]) =
      at[EnumeratedRange[F]](show.show)
    implicit def caseEvery[F <: CronField](implicit show: Show[SteppingRange[F]]) =
      at[SteppingRange[F]](show.show)
  }

}
