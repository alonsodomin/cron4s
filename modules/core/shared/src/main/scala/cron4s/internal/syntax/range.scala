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

package cron4s.internal.syntax

import cron4s.{CronField, CronUnit}
import cron4s.internal.base.Predicate
import cron4s.internal.expr.RangeExpr

import scala.language.higherKinds

/**
  * Created by alonsodomin on 25/08/2016.
  */
private[syntax] class RangeExprOps[E[_ <: CronField], F <: CronField](
    self: E[F],
    tc: RangeExpr[E, F]
) {

  def matches: Predicate[Int] = tc.matches(self)

  def implies[EE[_ <: CronField]](ee: EE[F])(implicit EE: RangeExpr[EE, F]): Boolean =
    tc.implies(self)(ee)

  def impliedBy[EE[_ <: CronField]](ee: EE[F])(implicit EE: RangeExpr[EE, F]): Boolean =
    tc.impliedBy(self)(ee)

}

private[syntax] trait RangeExprSyntax {

  implicit def toExprOps[E[_ <: CronField], F <: CronField](
      target: E[F]
  )(implicit tc: RangeExpr[E, F]): RangeExprOps[E, F] =
    new RangeExprOps[E, F](target, tc)

}

object range extends RangeExprSyntax
