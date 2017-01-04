package cron4s.types

import scala.language.higherKinds

/**
  * Created by alonsodomin on 23/08/2016.
  */
trait Enumerated[A] {

  def min(a: A): Int = range(a).head
  def max(a: A): Int = range(a).last

  def step(a: A)(from: Int, stepSize: Int): Option[(Int, Int)] = {
    val aRange = range(a)

    if (aRange.isEmpty) None
    else if (from < min(a) && stepSize >= 0) {
      Some(min(a) -> stepSize)
    } else if (from > max(a) && stepSize <= 0) {
      Some(max(a) -> stepSize)
    } else {
      val index = aRange.lastIndexWhere(from >= _)
      val pointer = index + stepSize

      val newIdx = {
        val mod = pointer % aRange.size
        if (mod < 0) aRange.size + mod
        else mod
      }
      val offsetPointer = if (pointer < 0) {
        pointer - (aRange.size - 1)
      } else {
        pointer
      }

      val newValue = aRange(newIdx)
      Some(newValue -> offsetPointer / aRange.size)
    }
  }

  def next(a: A)(from: Int): Option[Int] = step(a)(from, 1).map(_._1)
  def prev(a: A)(from: Int): Option[Int] = step(a)(from, -1).map(_._1)

  def range(a: A): IndexedSeq[Int]
}

object Enumerated {

  @inline def apply[A](implicit ev: Enumerated[A]): Enumerated[A] = ev

}
