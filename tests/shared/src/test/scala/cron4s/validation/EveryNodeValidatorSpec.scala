package cron4s.validation

import cron4s.{CronField, CronUnit}
import cron4s.expr._
import cron4s.testkit.gen.NodeGenerators
import cron4s.types.{Enumerated, Expr}

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

/**
  * Created by alonsodomin on 30/12/2016.
  */
class EveryNodeValidatorSpec extends PropSpec
  with GeneratorDrivenPropertyChecks
  with Matchers
  with NodeGenerators {

  import CronField._

  private[this] def check[F <: CronField](
      implicit unit: CronUnit[F], enum: Enumerated[CronUnit[F]], expr: Expr[FrequencyBaseNode, F]
  ): Unit = {
    property(s"EveryNode[${unit.field}] with invalid base returns the invalid errors of its base") {
      forAll(invalidEveryGen[F]) { node =>
        val expectedErrors = NodeValidator[FrequencyBaseNode[F]].validate(node.value)
        val returnedErrors = NodeValidator[EveryNode[F]].validate(node)

        returnedErrors should contain allElementsOf expectedErrors
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
