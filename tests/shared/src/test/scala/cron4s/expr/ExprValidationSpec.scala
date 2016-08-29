package cron4s.expr

import cron4s._
import org.scalatest.{FlatSpec, Matchers}

import scalaz._

/**
  * Created by alonsodomin on 05/08/2016.
  */
class ExprValidationSpec extends FlatSpec with Matchers {
  import CronField._
  import validation._

  "A series of enumerable expressions" should "not be valid if there are expressions that imply each other" in {
    val expr1 = ConstExpr(Minute, 23)
    val expr2 = BetweenExpr(ConstExpr(Minute, 10), ConstExpr(Minute, 24))

    val result = validateSeveral[Minute.type](NonEmptyList(expr1, expr2))
    result shouldBe Failure(NonEmptyList(s"Expression $expr1 is implied by $expr2"))
  }

  it should "not fail if expressions overlap without full implication" in {
    val expr1 = BetweenExpr(ConstExpr(Minute, 6), ConstExpr(Minute, 12))
    val expr2 = BetweenExpr(ConstExpr(Minute, 10), ConstExpr(Minute, 24))

    val result = validateSeveral[Minute.type](NonEmptyList(expr1, expr2))
    result should matchPattern { case Success(SeveralExpr(_)) => }
  }

}
