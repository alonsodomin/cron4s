package cron4s.core

import scala.collection.immutable.SortedSet

/**
  * Created by alonsodomin on 31/12/2015.
  */
trait Sequential[T] extends Bound[T] {

  def next(a: T): Option[T] = forward(a, 1).map(_._1)
  def previous(a: T): Option[T] = rewind(a, 1).map(_._1)

  def step(from: T, step: Int): Option[(T, Int)]

  def forward(a: T, amount: Int): Option[(T, Int)] = step(a, amount)
  def rewind(a: T, amount: Int): Option[(T, Int)] = step(a, -amount)

}

object Sequential {
  @inline def apply[T](implicit ev: Sequential[T]): Sequential[T] = ev

  def sequential(seq: IndexedSeq[Int]): Sequential[Int] = new Sequential[Int] {
    def step(v: Int, amount: Int): Option[(Int, Int)] = {
      val index = seq.lastIndexWhere(v >= _)
      val cursor = index + amount
      val newIdx = {
        val mod = cursor % seq.size
        if (mod < 0) seq.size  + mod
        else mod
      }
      val newValue = seq(newIdx)
      Some(newValue, cursor / seq.size)
    }

    override val max: Int = seq.last

    override val min: Int = seq.head
  }
}