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

import cron4s.base.{Direction, Enumerated}
import cron4s.syntax.enumerated._
import cron4s.testkit._

import org.scalacheck.Prop

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 27/08/2016.
  */
trait EnumeratedLaws[A] {
  implicit def TC: Enumerated[A]

  def min(a: A): IsEqual[Int] =
    a.min <-> a.range.min

  def max(a: A): IsEqual[Int] =
    a.max <-> a.range.max

  def forward(a: A, from: Int): IsEqual[Option[Int]] =
    a.next(from) <-> a.step(from, 1).map(_._1)

  def backwards(a: A, from: Int): IsEqual[Option[Int]] =
    a.prev(from) <-> a.step(from, -1).map(_._1)

  private[cron4s] def zeroStepSize(a: A, from: Int, direction: Direction): Boolean = {
    val stepped = TC.step0(a, from, 0, direction)
    stepped.forall { case (result, _) => a.range.contains(result) }
  }

  def fromMinToMinForwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.min, a.range.size) <-> Some(a.min -> 1)

  def fromMaxToMaxForwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.max, a.range.size) <-> Some(a.max -> 1)

  def fromMinToMaxForwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.min, a.range.size - 1) <-> Some(a.max -> 0)

  def fromMinToMaxBackwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.min, -1) <-> Some(a.max -> -1)

  def fromMaxToMinForwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.max, 1) <-> Some(a.min -> 1)

  def fromMaxToMinBackwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.max, -(a.range.size - 1)) <-> Some(a.min -> 0)

  def backAndForth(a: A, from: Int, stepSize: Int): Prop = {
    if (stepSize == 0) proved
    else {
      val moved = a.step(from, stepSize).map(_._1)
      val returned = moved.flatMap { from2 =>
        a.step(from2, stepSize * -1).map(_._1)
      }

      val expected = moved.map { _ =>
        if (stepSize == 0) from
        else {
          val idx = if (stepSize > 0) {
            val i = a.range.lastIndexWhere(from >= _)
            if (i == -1) a.range.size - 1
            else i
          } else {
            val i = a.range.indexWhere(from <= _)
            if (i == -1) 0
            else i
          }
          a.range(idx)
        }
      }

      returned ?== expected
    }
  }

}

object EnumeratedLaws {
  def apply[A](implicit ev: Enumerated[A]) = new EnumeratedLaws[A] { val TC = ev }
}
