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

package cron4s.base

import cats.Eq
import cats.laws.discipline.{ContravariantTests, MonoidKTests}

import cron4s.syntax.predicate._
import cron4s.testkit.Cron4sLawSuite
import cron4s.testkit.discipline.PredicateTests

import org.scalacheck._

/**
  * Created by alonsodomin on 04/08/2016.
  */
class PredicateSpec extends Cron4sLawSuite {
  import Arbitrary._

  implicit lazy val arbitraryPredicate = Arbitrary[Predicate[Int]] {
    for { x <- arbitrary[Int] } yield equalTo(x)
  }

  implicit val predicateEq = Eq.by[Predicate[Int], Boolean](_.apply(0))

  checkAll("ContravariantPredicate", ContravariantTests[Predicate].contravariant[Int, Int, Int])
  checkAll("PredicateConjunctionMonoid", MonoidKTests[Predicate](Predicate.conjunction.monoidK).monoidK[Int])
  checkAll("PredicateDisjunctionMonoid", MonoidKTests[Predicate](Predicate.disjunction.monoidK).monoidK[Int])

  checkAll("Predicate", PredicateTests.predicate[List, Int])

  /*val alwaysPredicates = for {
    returnVal <- arbitrary[Boolean]
    predicate <- Gen.const(always[Int](returnVal))
    value     <- arbitrary[Int]
  } yield (predicate, returnVal, value)

  property("always") = forAll(alwaysPredicates) {
    case (matcher, returnVal, value) => matcher(value) == returnVal
  }*/

}
