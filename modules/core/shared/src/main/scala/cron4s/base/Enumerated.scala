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
package base

import cats.implicits._

trait Enumerated[A] extends Steppable[A, Int] {

  def min(a: A): Int = range(a).min
  def max(a: A): Int = range(a).max

  def range(a: A): IndexedSeq[Int]

  final def step(a: A, from: Int, step: Step): Either[StepError, (Int, Int)] = {
    val aRange = range(a)

    def nearestNeighbourIndex = step.direction match {
      case Direction.Forward =>
        val idx = aRange.indexWhere(from < _)
        if (idx == -1) aRange.size
        else idx

      case Direction.Backwards =>
        aRange.lastIndexWhere(from > _)
    }

    def currentIdx =
      if (aRange.contains(from)) {
        aRange.indexOf(from)
      } else {
        val correction =
          if (step.amount != 0) step.direction.reverse.sign else 0
        nearestNeighbourIndex + correction
      }

    val pointer = currentIdx + (step.amount * step.direction.sign)
    val index = {
      val mod = pointer % aRange.size
      if (mod < 0) aRange.size + mod
      else mod
    }
    val offset = if (pointer < 0) {
      pointer - (aRange.size - 1)
    } else {
      pointer
    }

    val newValue  = aRange(index)
    val carryOver = offset / aRange.size

    (newValue, carryOver).asRight
  }

}

object Enumerated {

  @inline def apply[A](implicit ev: Enumerated[A]): Enumerated[A] = ev

}
