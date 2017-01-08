package cron4s.types

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 23/08/2016.
  */
trait Enumerated[A] {

  def min(a: A): Int = range(a).head
  def max(a: A): Int = range(a).last

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
