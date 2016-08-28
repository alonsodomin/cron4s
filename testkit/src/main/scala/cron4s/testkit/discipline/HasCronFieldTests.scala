package cron4s.testkit.discipline

import cron4s.CronField
import cron4s.testkit.laws.HasCronFieldLaws
import cron4s.types.HasCronField

import org.typelevel.discipline.Laws

import org.scalacheck.Arbitrary
import org.scalacheck.Prop
import Prop._

/**
  * Created by alonsodomin on 27/08/2016.
  */
trait HasCronFieldTests[A[_ <: CronField], F <: CronField] extends Laws {
  def laws: HasCronFieldLaws[A, F]

  def hasCronField(implicit arbAF: Arbitrary[A[F]], arbFrom: Arbitrary[Int]): RuleSet = new DefaultRuleSet(
    name = "hasCronField",
    parent = None,
    "min" -> forAll(laws.min _),
    "max" -> forAll(laws.max _),
    "forward" -> forAll(laws.forward _),
    "backwards" -> forAll(laws.backwards _),
    "step" -> forAll(laws.stepable _)
  )

}

object HasCronFieldTests {
  def apply[A[_ <: CronField], F <: CronField](implicit ev: HasCronField[A, F]): HasCronFieldTests[A, F] =
    new HasCronFieldTests[A, F] { def laws = HasCronFieldLaws[A, F] }
}
