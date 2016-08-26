package cron4s.types.syntax

import cron4s.CronField
import cron4s.types.{SequencedExpr, Predicate}

import scala.language.higherKinds

/**
  * Created by alonsodomin on 25/08/2016.
  */
private[syntax] class SequencedExprOps[E[_], F <: CronField](self: E[F], tc: SequencedExpr[E, F])
  extends SequencedFieldOps[E, F](self, tc) {

  def matches: Predicate[Int] = tc.matches(self)
  def impliedBy[EE[_]](expr: EE[F])(implicit ev: SequencedExpr[EE, F]) = tc.impliedBy(self)(expr)

}

private[syntax] trait SequencedExprSyntax extends SequencedFieldSyntax {

  implicit def toSequencedExprOps[E[_], F <: CronField]
      (target: E[F])
      (implicit tc: SequencedExpr[E, F]): SequencedExprOps[E, F] =
    new SequencedExprOps[E, F](target, tc)

}
