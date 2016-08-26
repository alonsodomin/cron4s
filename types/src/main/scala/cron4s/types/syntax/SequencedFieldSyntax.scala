package cron4s.types.syntax

import cron4s.CronField
import cron4s.types.SequencedField

import scala.language.higherKinds

/**
  * Created by alonsodomin on 23/08/2016.
  */
private[syntax] class SequencedFieldOps[C[_], F <: CronField](self: C[F], tc: SequencedField[C, F]) {
  def max: Int = tc.max(self)
  def min: Int = tc.min(self)
  def step(from: Int, stepSize: Int): Option[(Int, Int)] = tc.step(self)(from, stepSize)
  def next(from: Int): Option[Int] = tc.next(self)(from)
  def prev(from: Int): Option[Int] = tc.prev(self)(from)
}

private[syntax] trait SequencedFieldSyntax {

  implicit def toSequencedFieldOps[C[_], F <: CronField]
      (target: C[F])
      (implicit tc: SequencedField[C, F]): SequencedFieldOps[C, F] =
    new SequencedFieldOps[C, F](target, tc)

}
