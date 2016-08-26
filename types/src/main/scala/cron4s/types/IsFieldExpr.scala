package cron4s.types

import cron4s.CronField

import scala.language.higherKinds

/**
  * Created by alonsodomin on 25/08/2016.
  */
trait IsFieldExpr[E[_], F <: CronField] extends HasCronField[E, F] {

  def matches(e: E[F]): Predicate[Int]

  def impliedBy[E0, EE[_]](e: E[F])(expr: E0)(implicit apart: Apart.Aux[E0, F, EE], ops: IsFieldExpr[EE, F]): Boolean =
    range(e).forall(ops.matches(apart(expr)))

}

trait IsDivisibleExpr[E[_], F <: CronField] extends IsFieldExpr[E, F]
trait IsEnumerableExpr[E[_], F <: CronField] extends IsFieldExpr[E, F] with Ordering[E[F]]

object IsFieldExpr {
  @inline def apply[E[_], F <: CronField](implicit ev: IsFieldExpr[E, F]): IsFieldExpr[E, F] = ev
}
