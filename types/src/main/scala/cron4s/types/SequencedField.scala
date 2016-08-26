package cron4s.types

import cron4s.CronField

import scala.language.higherKinds

/**
  * Created by alonsodomin on 23/08/2016.
  */
trait SequencedField[FL[_], F <: CronField] {

  def min(fl: FL[F]): Int = range(fl).head
  def max(fl: FL[F]): Int = range(fl).last

  def step(fl: FL[F])(from: Int, stepSize: Int): Option[(Int, Int)] = {
    val flRange = range(fl)

    if (flRange.isEmpty) None
    else if (stepSize == 0) Some(from -> 0)
    else if (min(fl) == max(fl) && from >= max(fl)) {
      Some(min(fl) -> stepSize)
    } else {
      val index = flRange.lastIndexWhere(from >= _)
      val cursor = index + stepSize
      val newIdx = {
        val mod = cursor % flRange.size
        if (mod < 0) flRange.size + mod
        else mod
      }
      val newValue = flRange(newIdx)
      Some(newValue -> cursor / flRange.size)
    }
  }

  def next(fl: FL[F])(from: Int): Option[Int] = step(fl)(from, 1).map(_._1)
  def prev(fl: FL[F])(from: Int): Option[Int] = step(fl)(from, -1).map(_._1)

  def range(fL: FL[F]): IndexedSeq[Int]
}

object SequencedField {

  @inline def apply[FL[_], F <: CronField](implicit ev: SequencedField[FL, F]): SequencedField[FL, F] = ev

}
