package cron4s.testkit.discipline

import cron4s.testkit.laws.EnumeratedLaws
import cron4s.types.Enumerated

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

/**
  * Created by alonsodomin on 27/08/2016.
  */
trait EnumeratedTests[A] extends Laws {
  def laws: EnumeratedLaws[A]

  def enumerated(implicit arbAF: Arbitrary[A], arbFrom: Arbitrary[Int]): RuleSet = new DefaultRuleSet(
    name = "enumerated",
    parent = None,
    "min" -> forAll(laws.min _),
    "max" -> forAll(laws.max _),
    "forward" -> forAll(laws.forward _),
    "backwards" -> forAll(laws.backwards _),
    "step" -> forAll(laws.stepable _)
  )

}

object EnumeratedTests {
  def apply[A](implicit ev: Enumerated[A]): EnumeratedTests[A] =
    new EnumeratedTests[A] { val laws = EnumeratedLaws[A] }
}
