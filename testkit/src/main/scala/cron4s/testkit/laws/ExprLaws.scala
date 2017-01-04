package cron4s.testkit.laws

import cron4s.CronField
import cron4s.testkit._
import cron4s.types.Expr
import cron4s.syntax.expr._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ExprLaws[E[_ <: CronField], F <: CronField] extends EnumeratedLaws[E[F]] {
  implicit def TC: Expr[E, F]

  def matchable(expr: E[F], value: Int): IsEqual[Boolean] = {
    val withinRange = expr.range.contains(value)
    expr.matches(value) <-> withinRange
  }

  def implicationEquivalence[EE[_ <: CronField]](left: E[F], right: EE[F])(
      implicit
      ev: Expr[EE, F]
    ): IsEqual[Boolean] = {
      (left.impliedBy(right) && right.impliedBy(left)) <-> (left.range == right.range)
    }

}

object ExprLaws {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: Expr[E, F]) =
    new ExprLaws[E, F] { val TC = ev }
}
