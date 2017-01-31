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

import cron4s.syntax.predicate._

import org.scalacheck._

import scalaz._
import scalaz.scalacheck._
import Scalaz._

/**
  * Created by alonsodomin on 04/08/2016.
  */
object PredicateSpec extends Properties("Predicate") {
  import Prop._
  import Arbitrary._
  import ScalazProperties._

  implicit lazy val arbitraryMatcher = Arbitrary[Predicate[Int]] {
    for { x <- arbitrary[Int] } yield equalTo(x)
  }

  implicit val predicateEquality = Equal.equalBy[Predicate[Int], Boolean](_.apply(0))

  def checkAll(name: String, props: Properties) = {
    for ((name2, prop) <- props.properties) yield {
      property(name + ":" + name2) = prop
    }
  }

  checkAll("Predicate", contravariant.laws[Predicate])

  object disjunction {
    import Predicate.disjunction._

    def check() = {
      checkAll("Predicate", plusEmpty.laws[Predicate])
    }
  }
  object conjuction {
    import Predicate.conjunction._

    def check() = {
      checkAll("Predicate", plusEmpty.laws[Predicate])
    }
  }

  disjunction.check()
  conjuction.check()

  val preedicatesAndValues = for {
    predicate <- arbitrary[Predicate[Int]]
    value     <- arbitrary[Int]
  } yield (predicate, value)

  property("not") = forAll(preedicatesAndValues) {
    case (matcher, value) => (!matcher)(value) == !matcher(value)
  }

  val pairsOfPredicates = for {
    leftPred  <- arbitrary[Predicate[Int]]
    rightPred <- arbitrary[Predicate[Int]]
    value     <- arbitrary[Int]
  } yield (leftPred, rightPred, value)

  property("and") = forAll(pairsOfPredicates) {
    case (left, right, value) =>
      (left && right)(value) == (left(value) && right(value))
  }

  property("or") = forAll(pairsOfPredicates) {
    case (left, right, value) =>
      (left || right)(value) == (left(value) || right(value))
  }

  val alwaysPredicates = for {
    returnVal <- arbitrary[Boolean]
    predicate <- Gen.const(always[Int](returnVal))
    value     <- arbitrary[Int]
  } yield (predicate, returnVal, value)

  property("always") = forAll(alwaysPredicates) {
    case (matcher, returnVal, value) => matcher(value) == returnVal
  }

  val negatedPredicates = for {
    predicate <- arbitrary[Predicate[Int]]
    negated   <- Gen.const(not(predicate))
    value     <- arbitrary[Int]
  } yield (predicate, negated, value)

  property("negated") = forAll(negatedPredicates) {
    case (predicate, negated, value) =>
      negated(value) == !predicate(value)
  }

  val predicateList = for {
    list  <- Gen.listOf(arbitrary[Predicate[Int]])
    value <- arbitrary[Int]
  } yield (list, value)

  property("noneOf") = forAll(predicateList) {
    case (list, value) => noneOf(list).apply(value) == not(allOf(list))(value)
  }

  property("anyOf") = forAll(predicateList) {
    case (list, value) => anyOf(list).apply(value) == list.exists(_(value))
  }

  property("allOf") = forAll(predicateList) {
    case (list, value) => allOf(list).apply(value) == list.forall(_(value))
  }

}
