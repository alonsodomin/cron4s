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

import cats.Foldable
import cats.laws._
import cats.implicits._

import cron4s.base.Predicate
import cron4s.syntax.predicate._

/**
  * Created by alonsodomin on 10/04/2017.
  */
object PredicateLaws {

  def negation[A](self: Predicate[A], a: A): IsEq[Boolean] =
    (!self).apply(a) <-> !self.apply(a)

  def conjuction[A](self: Predicate[A], other: Predicate[A], value: A): IsEq[Boolean] =
    (self && other)(value) <-> (self(value) && other(value))

  def disjunction[A](self: Predicate[A], other: Predicate[A], value: A): IsEq[Boolean] =
    (self || other)(value) <-> (self(value) || other(value))

  def noMatch[F[_], A](preds: F[Predicate[A]], value: A)(implicit F: Foldable[F]): IsEq[Boolean] =
    noneOf(preds).apply(value) <-> not(allOf(preds))(value)

  def someMatch[F[_], A](preds: F[Predicate[A]], value: A)(implicit F: Foldable[F]): IsEq[Boolean] =
    anyOf(preds).apply(value) <-> preds.exists(_(value))

  def allMatch[F[_], A](preds: F[Predicate[A]], value: A)(implicit F: Foldable[F]): IsEq[Boolean] =
    allOf(preds).apply(value) <-> preds.forall(_(value))

}
