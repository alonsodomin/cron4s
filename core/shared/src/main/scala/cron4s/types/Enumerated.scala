package cron4s.types

import scala.language.higherKinds

/**
  * Created by alonsodomin on 23/08/2016.
  */
trait Enumerated[A] {

  def min(a: A): Int = range(a).head
  def max(a: A): Int = range(a).last

  def steppingUnit(a: A): Int = 1

  def step(a: A)(from: Int, stepSize: Int): Option[(Int, Int)] = {
    val aRange = range(a)

    if (aRange.isEmpty) None
    else if (from < min(a) && stepSize >= 0) {
      Some(min(a) -> (stepSize * steppingUnit(a)))
    } else if (from > max(a) && stepSize <= 0) {
      Some(max(a) -> (stepSize * steppingUnit(a)))
    } else {
      val index = aRange.lastIndexWhere(from >= _)
      val cursor = index + (stepSize * steppingUnit(a))
      val newIdx = {
        val mod = cursor % aRange.size
        if (mod < 0) aRange.size + mod
        else mod
      }
      val newValue = aRange(newIdx)
      Some(newValue -> cursor / aRange.size)
    }
  }

  def next(a: A)(from: Int): Option[Int] = step(a)(from, steppingUnit(a)).map(_._1)
  def prev(a: A)(from: Int): Option[Int] = step(a)(from, -steppingUnit(a)).map(_._1)

  def range(a: A): Vector[Int]
}

object Enumerated {

  @inline def apply[A](implicit ev: Enumerated[A]): Enumerated[A] = ev

}
