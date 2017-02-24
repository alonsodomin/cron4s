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

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 23/08/2016.
  */

private[cron4s] sealed abstract class Direction(val sign: Int) {
  def reverse: Direction
}
private[cron4s] object Direction {

  def of(step: Int): Direction = {
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

  private[cron4s] def step0(
      a: A, from: Int, stepSize: Int, direction: Direction
  ): Option[(Int, Int)] = {
    if (stepSize == Int.MinValue || stepSize == Int.MaxValue) {
      None
    } else {
      val aRange = range(a)

      def nearestNeighbourIndex = direction match {
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
        val correction = if (stepSize != 0) direction.reverse.sign else 0
        nearestNeighbourIndex + correction
      }

      val pointer = currentIdx + stepSize
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

      (aRange(index), offsetPointer / aRange.size).some
    }
  }

  def step(a: A)(from: Int, stepSize: Int): Option[(Int, Int)] =
    step0(a, from, stepSize, Direction.of(stepSize))

  def next(a: A)(from: Int): Option[Int] = step(a)(from, 1).map(_._1)
  def prev(a: A)(from: Int): Option[Int] = step(a)(from, -1).map(_._1)

  def range(a: A): IndexedSeq[Int]
}

object Enumerated {

  @inline def apply[A](implicit ev: Enumerated[A]): Enumerated[A] = ev

}
