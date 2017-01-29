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

import cron4s.CronField
import cron4s.expr.Expr
import cron4s.testkit._
import cron4s.syntax.expr._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ExprLaws[E[_ <: CronField], F <: CronField] extends EnumeratedLaws[E[F]] {
  implicit def TC: Expr[E, F]

  def matchable(expr: E[F], value: Int): IsEqual[Boolean] = {
    val withinRange = expr.range.contains(value)
    expr.matches(value) <-> withinRange
  }

  def implicationEquivalence[EE[_ <: CronField]](left: E[F], right: EE[F])(
      implicit
      ev: Expr[EE, F]
    ): IsEqual[Boolean] = {
      (left.impliedBy(right) && right.impliedBy(left)) <-> (left.range == right.range)
    }

}

object ExprLaws {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: Expr[E, F]) =
    new ExprLaws[E, F] { val TC = ev }
}
