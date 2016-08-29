package cron4s.testkit.discipline

import cron4s.CronField
import cron4s.expr.Expr
import cron4s.ext.DateTimeAdapter
import cron4s.testkit.laws.ExtendedExprLaws
import cron4s.types.IsFieldExpr
import org.scalacheck.Prop._
import org.scalacheck._
import org.typelevel.discipline.Laws

import scalaz.Equal

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ExtendedExprTests[E[_ <: CronField] <: Expr[_], F <: CronField, DateTime] extends Laws {
  def laws: ExtendedExprLaws[E, F, DateTime]

  def extendedExpr(implicit
      arbExExpr: Arbitrary[E[F]],
      arbDateTime: Arbitrary[DateTime],
      ev: IsFieldExpr[E, F]
  ): RuleSet = new DefaultRuleSet(
    name = "extendedExpr",
    parent = None,
    "forward" -> forAll(laws.forward _),
    "backwards" -> forAll(laws.backwards _),
    "matchable" -> forAll(laws.matchable _)
  )

}

object ExtendedExprTests {

  def apply[E[_ <: CronField] <: Expr[_], F <: CronField, DateTime](implicit
    adapterEv: DateTimeAdapter[DateTime],
    eqEv: Equal[DateTime],
    exprEv: IsFieldExpr[E, F]
  ): ExtendedExprTests[E, F, DateTime] =
    new ExtendedExprTests[E, F, DateTime] { def laws = ExtendedExprLaws[E, F, DateTime] }

}
