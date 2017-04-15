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

import cats.implicits._

/**
  * Created by alonsodomin on 23/08/2016.
  */
final case class Step private[cron4s] (amount: Int, direction: Direction) {
  require(amount >= 0, "Step amount must be a positive integer")

  def reverse: Step = copy(direction = direction.reverse)

}

object Step {
  def apply(stepSize: Int): Step = new Step(Math.abs(stepSize), Direction.ofSign(stepSize))
}

sealed abstract class Direction(private[cron4s] val sign: Int) {
  def reverse: Direction
}
object Direction {

  def ofSign(step: Int): Direction = {
    if (step >= 0) Forward
    else Backwards
  }

  case object Forward extends Direction(1) {
    def reverse: Direction = Backwards
  }
  case object Backwards extends Direction(-1) {
    def reverse: Direction = Forward
  }
}

trait Enumerated[A] {

  def min(a: A): Int = range(a).min
  def max(a: A): Int = range(a).max

  def step(a: A, from: Int, step: Step): Option[(Int, Int)] = {
    if (step.amount == Int.MinValue || step.amount == Int.MaxValue) None
    else {
      val aRange = range(a)

      def nearestNeighbourIndex = step.direction match {
        case Direction.Forward =>
          val idx = aRange.indexWhere(from < _)
          if (idx == -1) aRange.size
          else idx

        case Direction.Backwards =>
          aRange.lastIndexWhere(from > _)
      }

      def currentIdx = if (aRange.contains(from)) {
        aRange.indexOf(from)
      } else {
        nearestNeighbourIndex
      }

      val pointer = currentIdx + (step.amount * step.direction.sign)
      val index = {
        val mod = pointer % aRange.size
        if (mod < 0) aRange.size + mod
        else mod
      }
      val offsetPointer = if (pointer < 0) {
        pointer - (aRange.size - 1)
      } else {
        pointer
      }

      val newValue = aRange(index)
      if (newValue != from) (newValue, offsetPointer / aRange.size).some
      else none
    }
  }

  def step(a: A)(from: Int, stepSize: Int): Option[(Int, Int)] = {
    if (stepSize == Int.MinValue || stepSize == Int.MaxValue) None
    else step(a, from, Step(stepSize))
  }

  def next(a: A)(from: Int): Option[Int] = step(a)(from, 1).map(_._1)
  def prev(a: A)(from: Int): Option[Int] = step(a)(from, -1).map(_._1)

  def range(a: A): IndexedSeq[Int]
}

object Enumerated {

  @inline def apply[A](implicit ev: Enumerated[A]): Enumerated[A] = ev

}
