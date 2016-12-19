package cron4s.types

import cron4s.CronField
import cron4s.CronUnit

import scala.language.higherKinds

/**
  * Created by alonsodomin on 25/08/2016.
  */
trait IsFieldExpr[E[_ <: CronField], F <: CronField] extends HasCronField[E, F] {

  def matches(e: E[F]): Predicate[Int]

  def impliedBy[EE[_ <: CronField]](e: E[F])(expr: EE[F])(implicit ops: IsFieldExpr[EE, F]): Boolean =
    range(e).forall(ops.matches(expr))

  def show(e: E[F]): String

  def unit(e: E[F]): CronUnit[F]

}

object IsFieldExpr {
  @inline def apply[E[_ <: CronField], F <: CronField](implicit ev: IsFieldExpr[E, F]): IsFieldExpr[E, F] = ev
}
