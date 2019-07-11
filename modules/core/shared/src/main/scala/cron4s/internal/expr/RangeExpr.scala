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
package internal
package expr

import cron4s.{CronField, CronUnit}
import cron4s.internal.base._

import shapeless._

/**
  * Created by alonsodomin on 25/08/2016.
  */
trait RangeExpr[E[_ <: CronField], F0 <: CronField] extends HasCronUnit[E[F0]] with HasMatcher[E[F0]] with Productive[E[F0]] {
  type F = F0
  type X = Int

  def implies[EE[_ <: CronField]](e: E[F])(ee: EE[F])(implicit EE: RangeExpr[EE, F]): Boolean

  final def impliedBy[EE[_ <: CronField]](e: E[F])(ee: EE[F])(
      implicit EE: RangeExpr[EE, F]
  ): Boolean =
    EE.implies(ee)(e)(this)

  @deprecated("Use unfold instead", "0.6.0")
  def range(e: E[F]): IndexedSeq[Int] = unfold(e).toVector

}

object RangeExpr {
  @inline def apply[E[_ <: CronField], F <: CronField](
      implicit ev: RangeExpr[E, F]
  ): RangeExpr[E, F] = ev
}

trait RangeExprDerivation0 {

  //implicit def deriveRangeExprCNil[E[_ <: CronField], F0 <: CronField]: RangeExpr[E, F]

}