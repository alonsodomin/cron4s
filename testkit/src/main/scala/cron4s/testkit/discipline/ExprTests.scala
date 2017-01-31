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

import cron4s.CronField
import cron4s.expr.Expr
import cron4s.testkit.laws.ExprLaws

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ExprTests[E[_ <: CronField], F <: CronField] extends EnumeratedTests[E[F]] {
  def laws: ExprLaws[E, F]

  def expr[EE[_ <: CronField]](
    implicit
    arbEF: Arbitrary[E[F]],
    arbEEF: Arbitrary[EE[F]],
    arbFrom: Arbitrary[Int],
    e: Expr[EE, F]
  ): RuleSet = new DefaultRuleSet(
    name = "expr",
    parent = Some(enumerated),
    "matchable" -> forAll(laws.matchable _),
    "implication" -> forAll(laws.implicationEquivalence[EE] _)
  )

}

object ExprTests {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: Expr[E, F]): ExprTests[E, F] =
    new ExprTests[E, F] { val laws = ExprLaws[E, F] }
}
