package cron4s.validation

import cron4s._
import cron4s.expr._
import cron4s.testkit.gen._

import org.scalatest._
import org.scalatest.prop._

import org.scalacheck._

object NodeValidatorSpec {

  // trait ExprTables[F <: CronField] extends Tables {
  //
  //   def sample = Table(("expr", "errors"), (fieldExpr, errors))
  //
  //   def fieldExpr: Expr[F]
  //   def errors: List[FieldError]
  // }
  //
  // class EachExprSample[F <: CronField] extends ExprTables[F] {
  //   val fieldExpr = EachExpr[F]
  //   val errors = List.empty[FieldError]
  // }

}

class NodeValidatorSpec extends PropSpec with GeneratorDrivenPropertyChecks with ArbitraryEachNode with ArbitraryConstNode with Matchers {
  import CronField._



  property("each expressions should always pass validation") {
    forAll { (expr: EachNode[Second]) =>
      NodeValidator[EachNode, Second].validate(expr) shouldBe List.empty[FieldError]
    }
  }

  property("const expressions should pass validation when value is within range") {
    forAll { (expr: ConstNode[Second]) =>
      NodeValidator[ConstNode, Second].validate(expr) shouldBe List.empty[FieldError]
    }
  }

}
