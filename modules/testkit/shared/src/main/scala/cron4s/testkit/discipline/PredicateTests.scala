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

import cats.Foldable
import cats.laws.discipline._
import cats.implicits._

import cron4s.base.Predicate
import cron4s.testkit.laws.PredicateLaws

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._

import org.typelevel.discipline.Laws

/** Created by alonsodomin on 10/04/2017.
  */
object PredicateTests extends Laws {
  def predicate[F[_]: Foldable, A](implicit
      arbPred: Arbitrary[Predicate[A]],
      arbFold: Arbitrary[F[Predicate[A]]],
      arbA: Arbitrary[A]
  ): RuleSet =
    new DefaultRuleSet(
      name = "Predicate",
      parent = None,
      "negation"    -> forAll(PredicateLaws.negation[A] _),
      "conjunction" -> forAll(PredicateLaws.conjuction[A] _),
      "disjunction" -> forAll(PredicateLaws.disjunction[A] _),
      "noMatch"     -> forAll(PredicateLaws.noMatch[F, A] _),
      "someMatch"   -> forAll(PredicateLaws.someMatch[F, A] _),
      "allMatch"    -> forAll(PredicateLaws.allMatch[F, A] _)
    )
}
