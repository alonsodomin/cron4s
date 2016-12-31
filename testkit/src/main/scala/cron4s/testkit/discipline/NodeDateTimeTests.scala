package cron4s.testkit.discipline

import cron4s.CronField
import cron4s.spi.DateTimeAdapter
import cron4s.testkit.laws.NodeDateTimeLaws
import cron4s.types.Expr

import org.scalacheck.Prop._
import org.scalacheck._

import org.typelevel.discipline.Laws

import scalaz.Equal

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait NodeDateTimeTests[E[_ <: CronField], F <: CronField, DateTime] extends Laws {
  def laws: NodeDateTimeLaws[E, F, DateTime]

  def dateTime(implicit
    arbNode: Arbitrary[E[F]],
    arbDateTime: Arbitrary[DateTime],
    expr: Expr[E, F]
  ): RuleSet = new DefaultRuleSet(
    name = "dateTime",
    parent = None,
    "forward" -> forAll(laws.forward _),
    "backwards" -> forAll(laws.backwards _),
    "matchable" -> forAll(laws.matchable _)
  )

}

object NodeDateTimeTests {

  def apply[E[_ <: CronField], F <: CronField, DateTime](implicit
    adapterEv: DateTimeAdapter[DateTime],
    eqEv: Equal[DateTime],
    exprEv: Expr[E, F]
  ): NodeDateTimeTests[E, F, DateTime] =
    new NodeDateTimeTests[E, F, DateTime] {
      val laws = NodeDateTimeLaws[E, F, DateTime]
    }

}
