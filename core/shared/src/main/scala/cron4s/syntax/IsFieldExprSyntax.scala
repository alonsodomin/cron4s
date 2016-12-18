package cron4s.syntax

import cron4s.{CronField, CronUnit}
import cron4s.types.{IsFieldExpr, Predicate}

import scala.language.higherKinds

/**
  * Created by alonsodomin on 25/08/2016.
  */
private[syntax] class IsFieldExprOps[E[_ <: CronField], F <: CronField](self: E[F], tc: IsFieldExpr[E, F])
  extends HasCronFieldOps[E, F](self, tc) {

  def matches: Predicate[Int] = tc.matches(self)

  def impliedBy[EE[_ <: CronField]](expr: EE[F])(implicit ops: IsFieldExpr[EE, F]): Boolean =
    tc.impliedBy(self)(expr)

  def unit: CronUnit[F] = tc.unit(self)

}

private[syntax] trait IsFieldExprSyntax extends HasCronFieldSyntax {

  implicit def toIsFieldExprOps[E[_ <: CronField], F <: CronField]
      (target: E[F])
      (implicit tc: IsFieldExpr[E, F]): IsFieldExprOps[E, F] =
    new IsFieldExprOps[E, F](target, tc)

}

object expr extends IsFieldExprSyntax