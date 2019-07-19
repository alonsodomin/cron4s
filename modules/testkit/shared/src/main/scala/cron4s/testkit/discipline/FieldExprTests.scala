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

package cron4s.testkit.discipline

import cats.laws.discipline._
import cats.implicits._

import cron4s.CronField
import cron4s.expr.FieldExpr
import cron4s.testkit.laws.FieldExprLaws

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait FieldExprTests[E[_ <: CronField], F <: CronField] extends EnumeratedTests[E[F]] {
  def laws: FieldExprLaws[E, F]

  def expr[EE[_ <: CronField]](
      implicit
      arbEF: Arbitrary[E[F]],
      arbEEF: Arbitrary[EE[F]],
      arbFrom: Arbitrary[Int],
      e: FieldExpr[EE, F]
  ): RuleSet = new DefaultRuleSet(
    name = "expr",
    parent = Some(enumerated),
    "matchable"              -> forAll(laws.matchable _),
    "implicationCommutative" -> forAll(laws.implicationCommutative[EE] _),
    "implicationEquivalence" -> forAll(laws.implicationEquivalence[EE] _)
  )

}

object FieldExprTests {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: FieldExpr[E, F]): FieldExprTests[E, F] =
    new FieldExprTests[E, F] { val laws = FieldExprLaws[E, F] }
}
