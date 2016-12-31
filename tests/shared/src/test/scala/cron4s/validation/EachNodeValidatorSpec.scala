package cron4s.validation

import cron4s._
import cron4s.expr._
import cron4s.testkit.gen._

import org.scalatest._
import org.scalatest.prop._

import org.scalacheck._

class EachNodeValidatorSpec extends PropSpec
  with GeneratorDrivenPropertyChecks
  with ArbitraryEachNode
  with Matchers {

  import CronField._

  private[this] def check[F <: CronField](implicit unit: CronUnit[F], arbNode: Arbitrary[EachNode[F]]): Unit = {
    property(s"EachNode[${unit.field}] should always pass validation") {
      forAll { (node: EachNode[F]) =>
        NodeValidator[EachNode[F]].validate(node) shouldBe List.empty[FieldError]
      }
    }
  }

  check[Second]
  check[Minute]
  check[Hour]
  check[DayOfMonth]
  check[Month]
  check[DayOfWeek]

}
