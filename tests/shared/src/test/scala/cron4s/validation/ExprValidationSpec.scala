package cron4s.validation

import cron4s._
import cron4s.expr.{BetweenNode, ConstNode, SeveralNode}

import org.scalatest._

import scalaz._

/**
  * Created by alonsodomin on 05/08/2016.
  */
@Ignore
class ExprValidationSpec extends FlatSpec with Matchers {
  import CronField._

  "A series of enumerable expressions" should "not be valid if there are expressions that imply each other" in {
    val expr1 = ConstNode[Minute](23)
    val expr2 = BetweenNode(ConstNode[Minute](10), ConstNode[Minute](24))

    /*val result = validateSeveral[Minute](NonEmptyList(expr1, expr2))
    result shouldBe Failure(NonEmptyList(
      InvalidFieldExpr(Minute, s"Expression '$expr1' at field Minute is implied by '$expr2'")
    ))*/
  }

  it should "not fail if expressions overlap without full implication" in {
    val expr1 = BetweenNode(ConstNode[Minute](6), ConstNode[Minute](12))
    val expr2 = BetweenNode(ConstNode[Minute](10), ConstNode[Minute](24))

    /*val result = validateSeveral[Minute](NonEmptyList(expr1, expr2))
    result should matchPattern { case Success(SeveralExpr(_)) => }*/
  }

}
