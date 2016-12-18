package cron4s.testkit.laws

import cron4s.CronField
import cron4s.types.IsFieldExpr
import cron4s.syntax.expr._

import scalaz.Scalaz._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait IsFieldExprLaws[E[_ <: CronField], F <: CronField] extends HasCronFieldLaws[E, F] {
  implicit def TC: IsFieldExpr[E, F]

  def matchable(expr: E[F], value: Int): Boolean = {
    val withinRange = expr.range.contains(value)
    expr.matches(value) === withinRange
  }

  def implicationEquivalence[EE[_ <: CronField]](left: E[F], right: EE[F])(implicit ev: IsFieldExpr[EE, F]): Boolean = {
    (left.impliedBy(right) && right.impliedBy(left)) === (left.range == right.range)
  }

}

object IsFieldExprLaws {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: IsFieldExpr[E, F]) =
    new IsFieldExprLaws[E, F] { val TC = ev }
}
