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

import cats.laws._

import cron4s.StepError
import cron4s.base.Enumerated
import cron4s.syntax.steppable._
import cron4s.syntax.enumerated._

/**
  * Created by alonsodomin on 27/08/2016.
  */
trait EnumeratedLaws[A] {
  implicit def TC: Enumerated[A]

  def min(a: A): IsEq[Int] =
    a.min <-> a.range.min

  def max(a: A): IsEq[Int] =
    a.max <-> a.range.max

  def forward(a: A, from: Int): IsEq[Option[Int]] =
    a.next(from) <-> a.step(from, 1).map(_._1).toOption

  def backwards(a: A, from: Int): IsEq[Option[Int]] =
    a.prev(from) <-> a.step(from, -1).map(_._1).toOption

  def fromMinToMinForwards(a: A): IsEq[Option[(Int, Int)]] =
    a.step(a.min, a.range.size).toOption <-> Some(a.min -> 1)

  def fromMaxToMaxForwards(a: A): IsEq[Option[(Int, Int)]] =
    a.step(a.max, a.range.size).toOption <-> Some(a.max -> 1)

  def fromMinToMaxForwards(a: A): IsEq[Option[(Int, Int)]] = {
    val expected = if (a.range.size == 1) None else Some(a.max -> 0)
    a.step(a.min, a.range.size - 1).toOption <-> expected
  }

  def fromMinToMaxBackwards(a: A): IsEq[Option[(Int, Int)]] =
    a.step(a.min, -1).toOption <-> Some(a.max -> -1)

  def fromMaxToMinForwards(a: A): IsEq[Option[(Int, Int)]] =
    a.step(a.max, 1).toOption <-> Some(a.min -> 1)

  def fromMaxToMinBackwards(a: A): IsEq[Option[(Int, Int)]] = {
    val expected = if (a.range.size == 1) None else Some(a.min -> 0)
    a.step(a.max, -(a.range.size - 1)).toOption <-> expected
  }

}

object EnumeratedLaws {
  def apply[A](implicit ev: Enumerated[A]) = new EnumeratedLaws[A] {
    val TC = ev
  }
}
