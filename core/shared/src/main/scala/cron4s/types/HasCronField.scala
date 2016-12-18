package cron4s.types

import cron4s.CronField

import scala.language.higherKinds

/**
  * Created by alonsodomin on 23/08/2016.
  */
trait HasCronField[A[_ <: CronField], F <: CronField] {

  def min(a: A[F]): Int = range(a).head
  def max(a: A[F]): Int = range(a).last

  def steppingUnit(a: A[F]): Int = 1

  def step(a: A[F])(from: Int, stepSize: Int): Option[(Int, Int)] = {
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

  def next(a: A[F])(from: Int): Option[Int] = step(a)(from, steppingUnit(a)).map(_._1)
  def prev(a: A[F])(from: Int): Option[Int] = step(a)(from, -steppingUnit(a)).map(_._1)

  def range(a: A[F]): Vector[Int]
}

object HasCronField {

  @inline def apply[A[_ <: CronField], F <: CronField]
      (implicit ev: HasCronField[A, F]): HasCronField[A, F] = ev

}
