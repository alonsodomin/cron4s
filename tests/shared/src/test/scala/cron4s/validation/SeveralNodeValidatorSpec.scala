package cron4s.validation

import cron4s.expr.{EnumerableNode, SeveralNode}
import cron4s.{CronField, CronUnit, FieldError}
import cron4s.testkit.gen.NodeGenerators
import cron4s.types.Enumerated
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

/**
  * Created by alonsodomin on 29/12/2016.
  */
class SeveralNodeValidatorSpec extends PropSpec
  with GeneratorDrivenPropertyChecks
  with Matchers
  with NodeGenerators {

  import CronField._

  private[this] def check[F <: CronField](implicit unit: CronUnit[F], ev: Enumerated[CronUnit[F]]): Unit = {
    property(s"SeveralNode[${unit.field}] with valid components should pass validation") {
      forAll(severalGen[F]) { node =>
        NodeValidator[SeveralNode[F]].validate(node) shouldBe List.empty[FieldError]
      }
    }

    property(s"SeveralNode[${unit.field}] with invalid members should contain the errors of its elements") {
      forAll(invalidSeveralGen[F]) { node =>
        val expectedMemberErrors = node.values.list.toList.view.flatMap(NodeValidator[EnumerableNode[F]].validate)
        NodeValidator[SeveralNode[F]].validate(node) should contain allElementsOf expectedMemberErrors
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
