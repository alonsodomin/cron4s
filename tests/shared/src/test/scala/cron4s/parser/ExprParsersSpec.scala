package cron4s.parser

import cron4s.CronField
import cron4s.expr._
import cron4s.types.syntax.expr._
import org.scalacheck._

/**
  * Created by alonsodomin on 13/01/2016.
  */
object ExprParsersSpec extends Properties("ExprParsers") with ExprParsers with InputGenerators {
  import Prop._
  import cron4s.CronUnit._

  def verifyParsed[F <: CronField, P <: Expr[F]](parser: Parser[P], input: String)(verify: P => Boolean): Boolean = {
    parseAll(parser, input) match {
      case Success(parsed, _) => verify(parsed)
      case NoSuccess(msg, _) =>
        println(msg)
        false
    }
  }

  /** Utility method to help with type inference */
  def verifyConst[F <: CronField](parser: Parser[ConstExpr[F]], input: String)(verify: ConstExpr[F] => Boolean): Boolean =
    verifyParsed[F, ConstExpr[F]](parser, input)(verify)

  property("should be able to parse minutes") = forAll(Gen.choose(0, 59)) {
    x => verifyConst(minute, x.toString) { expr => expr.value == x }
  }

  property("should be able to parse hours") = forAll(Gen.choose(0, 23)) {
    x => verifyConst(hour, x.toString) { expr => expr.value == x }
  }

  property("should be able to parse days of month") = forAll(Gen.choose(1, 31)) {
    x => verifyConst(dayOfMonth, x.toString) { expr => expr.value == x }
  }

  property("should be able to parse numeric months") = forAll(Gen.choose(1, 12)) {
    x => verifyConst(month, x.toString) { expr => expr.value == x && expr.textValue.isEmpty }
  }
  property("should be able to parse named months") = forAll(Gen.oneOf(Months.textValues)) {
    x => verifyConst(month, x) { expr =>
      expr.textValue.contains(x) && expr.matches(Months.textValues.indexOf(x) + 1)
    }
  }

  property("should be able to parse numeric days of week") = forAll(Gen.choose(0, 6)) {
    x => verifyConst(dayOfWeek, x.toString) { _.value == x }
  }
  property("should be able to parse named days of week") = forAll(Gen.oneOf(DaysOfWeek.textValues)) {
    x => verifyConst(dayOfWeek, x) { expr =>
      expr.textValue.contains(x) && expr.matches(DaysOfWeek.textValues.indexOf(x))
    }
  }

}
