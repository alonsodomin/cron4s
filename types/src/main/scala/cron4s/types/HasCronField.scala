package cron4s.types

import cron4s.CronField

import scala.language.higherKinds

/**
  * Created by alonsodomin on 23/08/2016.
  */
trait HasCronField[A[_ <: CronField], F <: CronField] {

  def min(a: A[F]): Int = range(a).head
  def max(a: A[F]): Int = range(a).last

  def step(a: A[F])(from: Int, stepSize: Int): Option[(Int, Int)] = {
    val aRange = range(a)

    if (aRange.isEmpty) None
    else if (stepSize == 0) Some(from -> 0)
    else if (min(a) == max(a) && from >= max(a)) {
      Some(min(a) -> stepSize)
    } else {
      val index = aRange.lastIndexWhere(from >= _)
      val cursor = index + stepSize
      val newIdx = {
        val mod = cursor % aRange.size
        if (mod < 0) aRange.size + mod
        else mod
      }
      val newValue = aRange(newIdx)
      Some(newValue -> cursor / aRange.size)
    }
  }

  def next(a: A[F])(from: Int): Option[Int] = step(a)(from, 1).map(_._1)
  def prev(a: A[F])(from: Int): Option[Int] = step(a)(from, -1).map(_._1)

  def range(a: A[F]): IndexedSeq[Int]
}

object HasCronField {

  @inline def apply[A[_ <: CronField], F <: CronField](implicit ev: HasCronField[A, F]): HasCronField[A, F] = ev

  trait Laws[A[_ <: CronField], F <: CronField] {
    import syntax.field._

    implicit def A: HasCronField[A, F]


  }

}
