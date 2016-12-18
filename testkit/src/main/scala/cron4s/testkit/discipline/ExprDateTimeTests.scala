package cron4s.testkit.discipline

import cron4s.CronField
import cron4s.expr.Expr
import cron4s.spi.DateTimeAdapter
import cron4s.testkit.laws.ExprDateTimeLaws
import cron4s.types.IsFieldExpr

import org.scalacheck.Prop._
import org.scalacheck._
import org.typelevel.discipline.Laws

import scalaz.Equal

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ExprDateTimeTests[E[_ <: CronField], F <: CronField, DateTime] extends Laws {
  def laws: ExprDateTimeLaws[E, F, DateTime]

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

object ExprDateTimeTests {

  def apply[E[_ <: CronField], F <: CronField, DateTime](implicit
    adapterEv: DateTimeAdapter[DateTime],
    eqEv: Equal[DateTime],
    exprEv: IsFieldExpr[E, F]
  ): ExprDateTimeTests[E, F, DateTime] =
    new ExprDateTimeTests[E, F, DateTime] {
      val laws = ExprDateTimeLaws[E, F, DateTime]
    }

}
