package cron4s.types.syntax

import cron4s.CronField
import cron4s.types.HasCronField

import scala.language.higherKinds

/**
  * Created by alonsodomin on 23/08/2016.
  */
private[syntax] class HasCronFieldOps[C[_ <: CronField], F <: CronField](self: C[F], tc: HasCronField[C, F]) {
  def max: Int = tc.max(self)
  def min: Int = tc.min(self)
  def step(from: Int, stepSize: Int): Option[(Int, Int)] = tc.step(self)(from, stepSize)
  def next(from: Int): Option[Int] = tc.next(self)(from)
  def prev(from: Int): Option[Int] = tc.prev(self)(from)
  def members: IndexedSeq[Int] = tc.range(self)
}

private[syntax] trait HasCronFieldSyntax {

  implicit def toHasCronFieldOps[C[_ <: CronField], F <: CronField]
      (target: C[F])
      (implicit tc: HasCronField[C, F]): HasCronFieldOps[C, F] =
    new HasCronFieldOps[C, F](target, tc)

}

object field extends HasCronFieldSyntax
