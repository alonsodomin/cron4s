package cron4s.types

import cron4s.CronField

import scala.language.higherKinds

/**
  * Created by alonsodomin on 25/08/2016.
  */
trait SequencedExpr[E[_], F <: CronField] extends SequencedField[E, F] {

  def matches(e: E[F]): Predicate[Int]

  def impliedBy[EE[_]](e: E[F])(expr: EE[F])(implicit ev: SequencedExpr[EE, F]): Boolean =
    range(e).forall(expr.matches(_))

}

trait SeqDivisibleExpr[E[_], F <: CronField] extends SequencedExpr[E, F]
trait SeqEnumerableExpr[E[_], F <: CronField] extends SequencedExpr[E, F] with Ordering[E[F]]

object SequencedExpr {
  @inline def apply[E[_], F <: CronField](implicit ev: SequencedExpr[E, F]): SequencedExpr[E, F] = ev
}
