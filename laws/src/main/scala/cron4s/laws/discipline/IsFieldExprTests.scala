package cron4s.laws.discipline

import cron4s.CronField
import cron4s.laws.laws.IsFieldExprLaws
import cron4s.types.IsFieldExpr

import org.scalacheck.Arbitrary
import org.scalacheck.Prop
import Prop._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait IsFieldExprTests[E[_ <: CronField], F <: CronField] extends HasCronFieldTests[E, F] {
  def laws: IsFieldExprLaws[E, F]

  def fieldExpr(implicit
      arbEF: Arbitrary[E[F]],
      //arbEEF: Arbitrary[EE[F]],
      arbFrom: Arbitrary[Int]
      //ev: IsFieldExpr[EE, F]
  ): RuleSet = new DefaultRuleSet(
    name = "fieldExpr",
    parent = Some(hasCronField),
    "matchable"   -> forAll(laws.matchable _)
    //"implication" -> forAll(laws.implication[EE] _)
  )

}

object IsFieldExprTests {
  def apply[E[_ <: CronField], F <: CronField](implicit ev: IsFieldExpr[E, F]): IsFieldExprTests[E, F] =
    new IsFieldExprTests[E, F] { def laws = IsFieldExprLaws[E, F] }
}
