package cron4s.laws.laws

import cron4s.CronField
import cron4s.types.IsFieldExpr
import cron4s.types.syntax.expr._

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait IsFieldExprLaws[E[_ <: CronField], F <: CronField] extends HasCronFieldLaws[E, F] {
  implicit def TC: IsFieldExpr[E, F]

  def matchable(expr: E[F], value: Int): Boolean = {
    val withinRange = expr.members.contains(value)
    expr.matches(value) === withinRange
  }

  def implication[EE[_ <: CronField]](left: E[F], right: EE[F])(implicit ev: IsFieldExpr[EE, F]): Boolean = {
    (left == right) === (left.impliedBy(right) && right.impliedBy(left))
  }

}

object IsFieldExprLaws {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: IsFieldExpr[E, F]) =
    new IsFieldExprLaws[E, F] { def TC = ev }
}
