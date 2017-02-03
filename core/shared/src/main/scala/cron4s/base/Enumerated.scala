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
trait Enumerated[A] {

  def min(a: A): Int = range(a).min
  def max(a: A): Int = range(a).max

  def step(a: A)(from: Int, stepSize: Int): Option[(Int, Int)] = {
    if (stepSize == Int.MinValue || stepSize == Int.MaxValue) {
      None
    } else {
      val aRange = range(a)

      val nearestNeighbourIndex = if (stepSize > 0) {
        aRange.lastIndexWhere(from >= _).some
      } else if (stepSize < 0) {
        val idx = aRange.indexWhere(from <= _)
        if (idx == -1) aRange.size.some
        else idx.some
      } else {
        none[Int]
      }

      nearestNeighbourIndex.map { idx =>
        val pointer = idx + stepSize
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

        aRange(index) -> offsetPointer / aRange.size
      } orElse {
        val result = {
          if (from <= min(a)) min(a)
          else if (from >= max(a)) max(a)
          else from
        }
        (result -> 0).some
      }
    }
  }

  def next(a: A)(from: Int): Option[Int] = step(a)(from, 1).map(_._1)
  def prev(a: A)(from: Int): Option[Int] = step(a)(from, -1).map(_._1)

  def range(a: A): IndexedSeq[Int]
}

object Enumerated {

  @inline def apply[A](implicit ev: Enumerated[A]): Enumerated[A] = ev

}
