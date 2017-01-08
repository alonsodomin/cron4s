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

package cron4s.syntax

import cron4s.types.Predicate

import scalaz.{Equal, Foldable}

/**
  * Created by alonsodomin on 29/07/2016.
  */
trait PredicateSyntax {

  def always[A](value: => Boolean): Predicate[A] = Predicate { _ => value }

  def not[A](m: Predicate[A]): Predicate[A] = Predicate { a => !m(a) }

  def equalTo[A: Equal](a: A): Predicate[A] = Predicate { b => implicitly[Equal[A]].equal(a, b) }

  def noneOf[C[_], A](c: C[Predicate[A]])(implicit ev: Foldable[C]): Predicate[A] =
    not(allOf(c))

  def anyOf[C[_], A](c: C[Predicate[A]])(implicit ev: Foldable[C]): Predicate[A] =
    Predicate { a => ev.any(c)(_(a)) }

  def allOf[C[_], A](c: C[Predicate[A]])(implicit ev: Foldable[C]): Predicate[A] =
    Predicate { a => ev.all(c)(_(a)) }

}

object predicate extends PredicateSyntax
