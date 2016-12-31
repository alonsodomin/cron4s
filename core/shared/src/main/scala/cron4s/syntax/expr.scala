package cron4s.syntax

import cron4s.{CronField, CronUnit}
import cron4s.types.{Expr, Predicate}

import scala.language.higherKinds

/**
  * Created by alonsodomin on 25/08/2016.
  */
private[syntax] class ExprOps[E[_ <: CronField], F <: CronField](self: E[F], tc: Expr[E, F])
  extends EnumeratedOps[E[F]](self, tc) {

  def matches: Predicate[Int] = tc.matches(self)

  def impliedBy[EE[_ <: CronField]](expr: EE[F])(implicit ops: Expr[EE, F]): Boolean =
    tc.impliedBy(self)(expr)

  def unit: CronUnit[F] = tc.unit(self)

}

private[syntax] trait ExprSyntax extends EnumeratedSyntax {

  implicit def toExprOps[E[_ <: CronField], F <: CronField]
      (target: E[F])
      (implicit tc: Expr[E, F]): ExprOps[E, F] =
    new ExprOps[E, F](target, tc)

}

object expr extends ExprSyntax
