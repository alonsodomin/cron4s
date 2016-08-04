package cron4s.ext

import cron4s.{CronField, CronUnit, IsCronUnit}
import cron4s.expr.{AnyExpr, Expr, ExprGenerators}
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
  }

  for { unit <- CronUnit.All } checkFieldOps(unit)

}
