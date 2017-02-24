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

import cron4s.base.{Direction, Enumerated}

import scala.language.higherKinds

/**
  * Created by alonsodomin on 23/08/2016.
  */
private[syntax] class EnumeratedOps[A](self: A, tc: Enumerated[A]) {
  def max: Int = tc.max(self)
  def min: Int = tc.min(self)
  def step(from: Int, stepSize: Int): Option[(Int, Int)] = tc.step(self)(from, stepSize)
  private[cron4s] def stepInDirection(from: Int, stepSize: Int, direction: Direction): Option[(Int, Int, Direction)] =
    tc.stepInDirection(self, from, stepSize, direction)
  def next(from: Int): Option[Int] = tc.next(self)(from)
  def prev(from: Int): Option[Int] = tc.prev(self)(from)
  def range: IndexedSeq[Int] = tc.range(self)
}

private[syntax] trait EnumeratedSyntax {

  implicit def toEnumeratedOps[A](target: A)
      (implicit tc: Enumerated[A]): EnumeratedOps[A] =
    new EnumeratedOps[A](target, tc)

}

object enumerated extends EnumeratedSyntax
