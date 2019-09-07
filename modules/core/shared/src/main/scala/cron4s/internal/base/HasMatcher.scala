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
package base

import cats.instances.list._

import shapeless._

private[cron4s] trait HasMatcher[A, X] {
  def matches(a: A): Predicate[X]
}
private[cron4s] object HasMatcher extends HasMatcherDerivation {

  def apply[A, X](implicit ev: HasMatcher[A, X]): HasMatcher[A, X] = ev

  def instance[A, X](f: A => Predicate[X]): HasMatcher[A, X] =
    new HasMatcher[A, X] {
      def matches(a: A): Predicate[X] = f(a)
    }
}

private[base] trait HasMatcherDerivation extends HasMatcherDerivation1 {
  implicit def deriveHasMatcher[A, X, C <: Coproduct](
      implicit
      G: Generic.Aux[A, C],
      HM: HasMatcher[C, X]
  ): HasMatcher[A, X] =
    HasMatcher.instance(a => HM.matches(G.to(a)))

}

private[base] trait HasMatcherDerivation1 extends HasMatcherDerivation0 {

  implicit def deriveHasMatcherCoproduct[H, T <: Coproduct, X](
      implicit
      headHasMatcher: HasMatcher[H, X],
      tailHasMatcher: HasMatcher[T, X]
  ): HasMatcher[H :+: T, X] =
    HasMatcher.instance { ht =>
      Predicate.anyOf(
        ht.head.map(headHasMatcher.matches).toList ++ ht.tail.map(tailHasMatcher.matches).toList
      )
    }

}

private[base] trait HasMatcherDerivation0 {

  implicit def deriveHasMatcherCNil[X]: HasMatcher[CNil, X] =
    HasMatcher.instance(_.impossible)

}
