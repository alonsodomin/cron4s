package cron4s.parser

import cron4s.expr._
import org.scalacheck._

/**
  * Created by alonsodomin on 13/01/2016.
  */
object ExprParsersSpec extends Properties("ExprParsers") with ExprParsers with ExprGenerators {
  import CronUnit._
  import Expr._
  import Prop._

  def verifyParsed[F <: CronField, P <: Expr[F]](parser: Parser[P], input: String)(verify: P => Boolean): Boolean = {
    parseAll(parser, input) match {
      case Success(parsed, _) => verify(parsed)
      case NoSuccess(msg, _) =>
        println(msg)
        false
    }
  }

  /** Utility method to help with type inference */
  def verifyScalar[F <: CronField](parser: Parser[ConstExpr[F]], input: String)(verify: ConstExpr[F] => Boolean): Boolean =
    verifyParsed[F, ConstExpr[F]](parser, input)(verify)

  property("Should be able to parse minutes") = forAll(Gen.choose(0, 59)) {
    x => verifyScalar(minute, x.toString) { expr => expr.value == x }
  }

  property("Should be able to parse hours") = forAll(Gen.choose(0, 23)) {
    x => verifyScalar(hour, x.toString) { expr => expr.value == x }
  }

  property("Should be able to parse days of month") = forAll(Gen.choose(1, 31)) {
    x => verifyScalar(dayOfMonth, x.toString) { expr => expr.value == x }
  }

  property("Should be able to parse numeric months") = forAll(Gen.choose(1, 12)) {
    x => verifyScalar(month, x.toString) { expr => expr.value == x && expr.textValue.isEmpty }
  }
  property("Should be able to parse named months") = forAll(Gen.oneOf(MonthsUnit.namedValues)) {
    x => verifyScalar(month, x) { expr =>
      expr.textValue.contains(x) && expr.matcher.matches(MonthsUnit.namedValues.indexOf(x) + 1)
    }
  }

  property("Should be able to parse numeric days of week") = forAll(Gen.choose(0, 6)) {
    x => verifyScalar(dayOfWeek, x.toString) { _.value == x }
  }

}
