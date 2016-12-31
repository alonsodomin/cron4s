package cron4s.types

import cron4s.CronField
import cron4s.CronUnit

import scala.language.higherKinds

import scalaz.Show

/**
  * Created by alonsodomin on 25/08/2016.
  */
trait Expr[E[_ <: CronField], F <: CronField] extends Enumerated[E[F]] with Show[E[F]] {

  def matches(e: E[F]): Predicate[Int]

  def impliedBy[EE[_ <: CronField]](e: E[F])(expr: EE[F])(
      implicit ops: Expr[EE, F]
    ): Boolean = {
      val exprRange = range(e)
      exprRange.size > 0 && exprRange.forall(ops.matches(expr))
    }

  def unit(e: E[F]): CronUnit[F]

}

object Expr {
  @inline def apply[E[_ <: CronField], F <: CronField](implicit ev: Expr[E, F]): Expr[E, F] = ev
}
