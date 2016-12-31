package cron4s.validation

import cron4s._
import cron4s.expr.{EachNode, EveryNode, DivisibleNode}
import cron4s.types.Expr
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by alonsodomin on 31/12/2016.
  */
class EveryNodeValidatorRegressionSpec extends FlatSpec with Matchers {
  import CronField._

  "An Every node" should "pass validation if it's evenly divided" in {
    val eachNode = EachNode[Second]
    val everyNode = EveryNode[Second](eachNode, 12)

    val returnedErrors = NodeValidator[EveryNode[Second]].validate(everyNode)
    returnedErrors shouldBe List.empty[FieldError]
  }

  it should "not pass validation if it is not evenly divided" in {
    val eachNode = EachNode[Second]
    val everyNode = EveryNode[Second](eachNode, 13)

    val expr = Expr[DivisibleNode, Second]
    val expectedError = FieldError(Second,
      s"Step '${everyNode.freq}' does not evenly divide the value '${expr.shows(everyNode.value)}' in field ${everyNode.unit}"
    )

    val returnedErrors = NodeValidator[EveryNode[Second]].validate(everyNode)
    returnedErrors shouldBe List(expectedError)
  }

}
