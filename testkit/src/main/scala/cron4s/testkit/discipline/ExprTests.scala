package cron4s.testkit.discipline

import cron4s.CronField
import cron4s.testkit.laws.ExprLaws
import cron4s.types.Expr

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ExprTests[E[_ <: CronField], F <: CronField] extends EnumeratedTests[E[F]] {
  def laws: ExprLaws[E, F]

  def expr[EE[_ <: CronField]](
    implicit
    arbEF: Arbitrary[E[F]],
    arbEEF: Arbitrary[EE[F]],
    arbFrom: Arbitrary[Int],
    e: Expr[EE, F]
  ): RuleSet = new DefaultRuleSet(
    name = "expr",
    parent = Some(enumerated),
    "matches values inside range" -> forAll(laws.matchable _),
    "implication" -> forAll(laws.implicationEquivalence[EE] _)
  )

}

object ExprTests {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: Expr[E, F]): ExprTests[E, F] =
    new ExprTests[E, F] { val laws = ExprLaws[E, F] }
}
