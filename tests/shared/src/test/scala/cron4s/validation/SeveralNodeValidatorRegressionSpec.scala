package cron4s.validation

import cron4s._
import cron4s.expr.{BetweenNode, ConstNode, SeveralNode}

import org.scalatest._

/**
  * Created by alonsodomin on 05/08/2016.
  */
class SeveralNodeValidatorRegressionSpec extends FlatSpec with Matchers {
  import CronField._

  "A series of nodes" should "not be valid if any of them implies the other" in {
    val node1 = ConstNode[Minute](23)
    val node2 = BetweenNode[Minute](ConstNode(10), ConstNode(24))
    val severalNode = SeveralNode[Minute](node1, node2)

    val returnedErrors = NodeValidator[SeveralNode[Minute]].validate(severalNode)

    returnedErrors shouldBe List(FieldError(
      Minute,
      s"Value '$node1' at field Minute is implied by '$node2'"
    ))
  }

  it should "not fail if they overlap without full implication" in {
    val node1 = BetweenNode[Minute](ConstNode(6), ConstNode(12))
    val node2 = BetweenNode[Minute](ConstNode(10), ConstNode(24))
    val severalNode = SeveralNode[Minute](node1, node2)

    val returnedErrors = NodeValidator[SeveralNode[Minute]].validate(severalNode)
    returnedErrors shouldBe List.empty[FieldError]
  }

}
