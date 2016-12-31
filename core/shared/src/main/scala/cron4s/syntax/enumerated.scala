package cron4s.syntax

import cron4s.types.Enumerated

import scala.language.higherKinds

/**
  * Created by alonsodomin on 23/08/2016.
  */
private[syntax] class EnumeratedOps[A](self: A, tc: Enumerated[A]) {
  def max: Int = tc.max(self)
  def min: Int = tc.min(self)
  def step(from: Int, stepSize: Int): Option[(Int, Int)] = tc.step(self)(from, stepSize)
  def next(from: Int): Option[Int] = tc.next(self)(from)
  def prev(from: Int): Option[Int] = tc.prev(self)(from)
  def range: IndexedSeq[Int] = tc.range(self)
}

private[syntax] trait EnumeratedSyntax {

  implicit def toEnumeratedOps[A](target: A)
      (implicit tc: Enumerated[A]): EnumeratedOps[A] =
    new EnumeratedOps[A](target, tc)

}

object enumerated extends EnumeratedSyntax
