package cron4s.validation

import cron4s.{CronField, CronUnit, FieldError}
import cron4s.expr.ConstNode
import cron4s.testkit.gen.NodeGenerators
import cron4s.types.Enumerated

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

/**
  * Created by alonsodomin on 29/12/2016.
  */
class ConstNodeValidatorSpec extends PropSpec
  with GeneratorDrivenPropertyChecks
  with Matchers
  with NodeGenerators {

  import CronField._

  private[this] def check[F <: CronField](implicit unit: CronUnit[F], ev: Enumerated[CronUnit[F]]): Unit = {
    property(s"ConstNode[${unit.field}] with value within range should pass validation") {
      forAll(constGen[F]) { node =>
        NodeValidator[ConstNode[F]].validate(node) shouldBe List.empty[FieldError]
      }
    }

    property(s"ConstNode[${unit.field}] with value outside range should return a field error") {
      forAll(invalidConstGen[F]) { node =>
        val expectedError = FieldError(
          unit.field,
          s"Value ${node.value} is out of bounds for field: ${unit.field}"
        )
        NodeValidator[ConstNode[F]].validate(node) shouldBe List(expectedError)
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
