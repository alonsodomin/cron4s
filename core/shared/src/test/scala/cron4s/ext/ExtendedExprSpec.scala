package cron4s.ext

import cron4s.{CronUnit, IsCronUnit}
import cron4s.expr.{Expr, ExprGenerators}
import org.scalacheck._

/**
  * Created by alonsodomin on 04/08/2016.
  */
object ExtendedExprSpec extends Properties("ExtendedExpr") with ExprGenerators {
  import Prop._
  import Arbitrary.arbitrary
  import testdummy._

  def checkFieldOps[U](unit: U)(implicit isUnit: IsCronUnit[U]) = {
    val resolved = isUnit(unit)

    val exprAndDateTime = for {
      expr <- Gen.const(createAny(unit))
      dt   <- arbitrary[DummyDateTime]
    } yield (expr, dt)

    property(s"next:${resolved.field}") = forAll(exprAndDateTime) {
      case (expr: Expr[_], dateTime: DummyDateTime) =>
        expr.next(dateTime) == expr.step(dateTime, 1)
    }

    property(s"previous:${resolved.field}") = forAll(exprAndDateTime) {
      case (expr: Expr[_], dateTime: DummyDateTime) =>
        expr.previous(dateTime) == expr.step(dateTime, -1)
    }

    val exprDateTimeAndValue = for {
      expr  <- Gen.const(createAny(unit))
      dt    <- arbitrary[DummyDateTime]
      value <- Gen.choose(resolved.min, resolved.max)
    } yield (expr, dt, value)

    property(s"matches:${resolved.field}") = forAll(exprDateTimeAndValue) {
      case (expr: Expr[_], dateTime: DummyDateTime, value: Int) =>
        val fieldVal = TestDummyAdapter.get(dateTime, resolved.field)
        expr.matchesIn(dateTime) == fieldVal.exists(x => expr.matches(x))
    }
  }

  for { unit <- CronUnit.All } checkFieldOps(unit)

}
