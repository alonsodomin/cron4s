package cron4s.types.syntax

import cron4s.CronField
import cron4s.types.{Apart, IsFieldExpr, Predicate}

import scala.language.higherKinds

/**
  * Created by alonsodomin on 25/08/2016.
  */
private[syntax] class IsFieldExprOps[E[_], F <: CronField](self: E[F], tc: IsFieldExpr[E, F])
  extends HasCronFieldOps[E, F](self, tc) {

  def matches: Predicate[Int] = tc.matches(self)
  def impliedBy[E0, EE[_]](expr: E0)(implicit apart: Apart.Aux[E0, F, EE], ops: IsFieldExpr[EE, F]) = tc.impliedBy[E0, EE](self)(expr)

}

private[syntax] trait IsFieldExprSyntax extends HasCronFieldSyntax {

  implicit def toIsFieldExprOps[E[_], F <: CronField]
      (target: E[F])
      (implicit tc: IsFieldExpr[E, F]): IsFieldExprOps[E, F] =
    new IsFieldExprOps[E, F](target, tc)

}
