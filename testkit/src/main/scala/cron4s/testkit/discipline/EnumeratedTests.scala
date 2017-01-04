package cron4s.testkit.discipline

import cron4s.testkit.laws.EnumeratedLaws
import cron4s.types.Enumerated

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

import scalaz._
import Scalaz._

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
    "zeroStepSize" -> forAll(laws.zeroStepSize _),
    "fromMinToMinForwards" -> forAll(laws.fromMinToMinForwards _),
    "fromMaxToMaxForwards" -> forAll(laws.fromMaxToMaxForwards _),
    "fromMinToMaxForwards" -> forAll(laws.fromMinToMaxForwards _),
    "fromMinToMaxBackwards" -> forAll(laws.fromMinToMaxBackwards _),
    "fromMaxToMinForwards" -> forAll(laws.fromMaxToMinForwards _),
    "fromMaxToMinBackwards" -> forAll(laws.fromMaxToMinBackwards _)
  )

}

object EnumeratedTests {
  def apply[A](implicit ev: Enumerated[A]): EnumeratedTests[A] =
    new EnumeratedTests[A] { val laws = EnumeratedLaws[A] }
}
