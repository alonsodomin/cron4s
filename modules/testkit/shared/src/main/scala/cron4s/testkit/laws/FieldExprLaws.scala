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

package cron4s.testkit.laws

import cats.laws._

import cron4s.CronField
import cron4s.expr.FieldExpr
import cron4s.syntax.enumerated._
import cron4s.syntax.field._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait FieldExprLaws[E[_ <: CronField], F <: CronField] extends EnumeratedLaws[E[F]] {
  implicit def TC: FieldExpr[E, F]

  def matchable(expr: E[F], value: Int): IsEq[Boolean] = {
    val withinRange = expr.range.contains(value)
    expr.matches(value) <-> withinRange
  }

  def implicationCommutative[EE[_ <: CronField]](left: E[F], right: EE[F])(
      implicit
      EE: FieldExpr[EE, F]
  ): IsEq[Boolean] =
    left.implies(right) <-> right.impliedBy(left)

  def implicationEquivalence[EE[_ <: CronField]](left: E[F], right: EE[F])(
      implicit
      EE: FieldExpr[EE, F]
  ): IsEq[Boolean] =
    (left.impliedBy(right) && right.impliedBy(left)) <-> (left.range == right.range)

}

object FieldExprLaws {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: FieldExpr[E, F]) =
    new FieldExprLaws[E, F] { val TC = ev }
}
