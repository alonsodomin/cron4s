package cron4s.testkit.discipline

import cron4s.CronField
import cron4s.testkit.laws.DateTimeAdapterLaws
import cron4s.ext.DateTimeAdapter
import org.scalacheck._
import Prop._
import cron4s.testkit.CronFieldValue
import org.typelevel.discipline.Laws

import scalaz.Equal

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait DateTimeAdapterTests[DateTime <: AnyRef] extends Laws {
  def laws: DateTimeAdapterLaws[DateTime]

  def dateTimeAdapter[F <: CronField](implicit
    arbDateTime: Arbitrary[DateTime],
    arbFieldValue: Arbitrary[CronFieldValue[F]]
  ): RuleSet = new DefaultRuleSet(
    name = "dateTimeAdapter",
    parent = None,
    "immutability" -> forAll(laws.immutability[F] _),
    "settable" -> forAll(laws.settable[F] _)
  )

}

object DateTimeAdapterTests {

  def apply[DateTime <: AnyRef](implicit
      adapterEv: DateTimeAdapter[DateTime],
      eqEv: Equal[DateTime]
  ): DateTimeAdapterTests[DateTime] =
    new DateTimeAdapterTests[DateTime] {
      def laws: DateTimeAdapterLaws[DateTime] = DateTimeAdapterLaws[DateTime]
    }

}
