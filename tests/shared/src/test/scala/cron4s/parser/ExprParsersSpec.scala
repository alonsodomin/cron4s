package cron4s.parser

import cron4s.CronField
import cron4s.expr._
import cron4s.testkit.gen.ExprGens
import cron4s.types.syntax.expr._

import fastparse.all._

import org.scalacheck._

/**
  * Created by alonsodomin on 13/01/2016.
  */
object ExprParsersSpec extends Properties("ExprParsers") with ExprGens with InputGenerators {
  import Prop._
  import Arbitrary._
  import cron4s.CronUnit._

  def verifyParsed[F <: CronField, E <: Expr[F]](parser: Parser[E], input: String)(verify: E => Boolean): Boolean = {
    parser.parse(input) match {
      case Parsed.Success(parsed, _) => verify(parsed)
      case err: Parsed.Failure =>
        println(ParseError(err).failure.msg)
        false
    }
  }

  // Utility method to help with type inference
  def verifyConst[F <: CronField](parser: Parser[ConstExpr[F]], input: String)(verify: ConstExpr[F] => Boolean): Boolean =
    verifyParsed[F, ConstExpr[F]](parser, input)(verify)

  // --------------------------------------------------------------
  // Generators of valid parsable values
  // --------------------------------------------------------------

  private def withLeadingZero(base: Int): Gen[String] = {
    if (base < 10) {
      arbitrary[Boolean].map { doubleDigit =>
        if (doubleDigit) {
          s"0$base"
        } else base.toString
      }
    } else base.toString
  }

  val secondsOrMinutesGen: Gen[String] = Gen.choose(0, 59).flatMap(withLeadingZero)

  val hoursGen: Gen[String] = Gen.choose(0, 23).flatMap(withLeadingZero)

  val daysOfMonthGen: Gen[String] = Gen.choose(1, 31).flatMap(withLeadingZero)

  val numericMonthsGen: Gen[String] = Gen.choose(1, 12).flatMap(withLeadingZero)

  // --------------------------------------------------------------
  // Properties for the individual parsers
  // --------------------------------------------------------------

  property("should be able to parse seconds") = forAll(secondsOrMinutesGen) {
    x => verifyConst(seconds, x) { expr => expr.value == x.toInt }
  }

  property("should be able to parse minutes") = forAll(secondsOrMinutesGen) {
    x => verifyConst(minutes, x) { expr => expr.value == x.toInt }
  }

  property("should be able to parse hours") = forAll(hoursGen) {
    x => verifyConst(hours, x) { expr => expr.value == x.toInt }
  }

  property("should be able to parse days of month") = forAll(daysOfMonthGen) {
    x => verifyConst(daysOfMonth, x) { expr => expr.value == x.toInt }
  }

  property("should be able to parse numeric months") = forAll(numericMonthsGen) {
    x => verifyConst(months, x.toString) { expr => expr.value == x.toInt && expr.textValue.isEmpty }
  }
  property("should be able to parse named months") = forAll(Gen.oneOf(Months.textValues)) {
    x => verifyConst(months, x) { expr =>
      expr.textValue.contains(x) && expr.matches(Months.textValues.indexOf(x) + 1)
    }
  }

  property("should be able to parse numeric days of week") = forAll(Gen.choose(0, 6)) {
    x => verifyConst(daysOfWeek, x.toString) { _.value == x }
  }
  property("should be able to parse named days of week") = forAll(Gen.oneOf(DaysOfWeek.textValues)) {
    x => verifyConst(daysOfWeek, x) { expr =>
      expr.textValue.contains(x) && expr.matches(DaysOfWeek.textValues.indexOf(x))
    }
  }

}
