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

import cron4s.testkit.laws.EnumeratedLaws
import cron4s.base.Enumerated

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

/**
  * Created by alonsodomin on 27/08/2016.
  */
trait EnumeratedTests[A] extends Laws {
  def laws: EnumeratedLaws[A]

  def enumerated(implicit arbAF: Arbitrary[A], arbFrom: Arbitrary[Int]): RuleSet = new DefaultRuleSet(
    name = "enumerated",
    parent = None,
    "min" -> forAll(laws.min _),
    "max" -> forAll(laws.max _),
    "forward" -> forAll(laws.forward _),
    "backwards" -> forAll(laws.backwards _),
    "fromMinToMinForwards" -> forAll(laws.fromMinToMinForwards _),
    "fromMaxToMaxForwards" -> forAll(laws.fromMaxToMaxForwards _),
    "fromMinToMaxForwards" -> forAll(laws.fromMinToMaxForwards _),
    "fromMinToMaxBackwards" -> forAll(laws.fromMinToMaxBackwards _),
    "fromMaxToMinForwards" -> forAll(laws.fromMaxToMinForwards _),
    "fromMaxToMinBackwards" -> forAll(laws.fromMaxToMinBackwards _)
  )

}

object EnumeratedTests {
  def apply[A](implicit ev: Enumerated[A]): EnumeratedTests[A] =
    new EnumeratedTests[A] { val laws = EnumeratedLaws[A] }
}
