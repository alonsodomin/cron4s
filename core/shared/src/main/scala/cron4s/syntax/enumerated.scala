package cron4s.syntax

import cron4s.CronField
import cron4s.types.Enumerated

import scala.language.higherKinds

/**
  * Created by alonsodomin on 23/08/2016.
  */
private[syntax] class EnumeratedOps[A[_ <: CronField], F <: CronField](self: A[F], tc: Enumerated[A, F]) {
  def max: Int = tc.max(self)
  def min: Int = tc.min(self)
  def step(from: Int, stepSize: Int): Option[(Int, Int)] = tc.step(self)(from, stepSize)
  def next(from: Int): Option[Int] = tc.next(self)(from)
  def prev(from: Int): Option[Int] = tc.prev(self)(from)
  def range: Vector[Int] = tc.range(self)
}

private[syntax] trait EnumeratedSyntax {

  implicit def toEnumeratedOps[A[_ <: CronField], F <: CronField]
      (target: A[F])
      (implicit tc: Enumerated[A, F]): EnumeratedOps[A, F] =
    new EnumeratedOps[A, F](target, tc)

}

object enumerated extends EnumeratedSyntax
