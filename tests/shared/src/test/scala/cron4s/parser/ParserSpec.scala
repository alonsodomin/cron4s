package cron4s.parser

import cron4s._
import cron4s.expr._
import cron4s.testkit.gen.{ArbitraryEachExpr, ExprGens}
import cron4s.types.syntax.expr._

import fastparse.all._

import org.scalacheck._

/**
  * Created by alonsodomin on 13/01/2016.
  */
object ParserSpec extends Properties("parser") with InputGenerators with ExprGens with ArbitraryEachExpr {
  import Prop._
  import Arbitrary._
  import CronField._
  import CronUnit._

  def verifyParsed[F <: CronField, E <: Expr[F]](parser: Parser[E], input: String)(verify: E => Boolean): Boolean = {
    parser.parse(input) match {
      case Parsed.Success(parsed, _) => verify(parsed)
      case err: Parsed.Failure =>
        println(ParseError(err).failure.msg)
        false
    }
  }

  // Utility methods to help with type inference

  def verifyConst[F <: CronField](parser: Parser[ConstExpr[F]], input: String)(verify: ConstExpr[F] => Boolean): Boolean =
    verifyParsed[F, ConstExpr[F]](parser, input)(verify)

  def verifyEach(parser: Parser[EachExpr[CronField]], input: String): Boolean =
    verifyParsed[CronField, EachExpr[CronField]](parser, input)(_ => true)

  def verifyBetween[F <: CronField](parser: Parser[BetweenExpr[F]], input: String)(verify: BetweenExpr[F] => Boolean): Boolean =
    verifyParsed[F, BetweenExpr[F]](parser, input)(verify)

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
  property("should be able to parse named months") = forAll(nameMonthsGen) {
    x => verifyConst(months, x) { expr =>
      expr.textValue.contains(x) && expr.matches(Months.textValues.indexOf(x) + 1)
    }
  }

  property("should be able to parse numeric days of week") = forAll(numericDaysOfWeekGen) {
    x => verifyConst(daysOfWeek, x) { _.value == x.toInt }
  }
  property("should be able to parse named days of week") = forAll(namedDaysOfWeekGen) {
    x => verifyConst(daysOfWeek, x) { expr =>
      expr.textValue.contains(x) && expr.matches(DaysOfWeek.textValues.indexOf(x))
    }
  }

  val eachParserGen: Gen[Parser[EachExpr[CronField]]] =
    Gen.oneOf(each[Second], each[Minute], each[Hour], each[DayOfMonth], each[Month], each[DayOfWeek])

  property("should be able to parse an asterisk in any field") = forAll(eachParserGen) { parser =>
    verifyEach(parser, "*")
  }

  property("should be able to parse ranges in seconds") = forAll(secondsOrMinutesRangeGen) {
    case (input, (start, end)) =>
      verifyBetween(between(seconds), input) { expr =>
        expr.begin.value == start && expr.end.value == end
      }
  }

  property("should be able to parse ranges in minutes") = forAll(secondsOrMinutesRangeGen) {
    case (input, (start, end)) =>
      verifyBetween(between(minutes), input) { expr =>
        expr.begin.value == start && expr.end.value == end
      }
  }

  property("should be able to parse ranges in hours") = forAll(hoursRangeGen) {
    case (input, (start, end)) =>
      verifyBetween(between(hours), input) { expr =>
        expr.begin.value == start && expr.end.value == end
      }
  }

  property("should be able to parse ranges in days of month") = forAll(daysOfMonthRangeGen) {
    case (input, (start, end)) =>
      verifyBetween(between(daysOfMonth), input) { expr =>
        expr.begin.value == start && expr.end.value == end
      }
  }

  property("should be able to parse ranges for numeric months") = forAll(numericMonthsRangeGen) {
    case (input, (start, end)) =>
      verifyBetween(between(months), input) { expr =>
        expr.begin.value == start && expr.end.value == end
      }
  }
  property("should be able to parse ranges for named months") = forAll(namedMonthsRangeGen) {
    case (input, (start, end)) =>
      verifyBetween(between(months), input) { expr =>
        expr.begin.textValue.contains(start) && expr.end.textValue.contains(end)
      }
  }

  property("should be able to parse ranges for numeric days of week") = forAll(numericDaysOfWeekRangeGen) {
    case (input, (start, end)) =>
      verifyBetween(between(daysOfWeek), input) { expr =>
        expr.begin.value == start && expr.end.value == end
      }
  }
  property("should be able to parse ranges for named days of week") = forAll(namedDaysOfWeekRangeGen) {
    case (input, (start, end)) =>
      verifyBetween(between(daysOfWeek), input) { expr =>
        expr.begin.textValue.contains(start) && expr.end.textValue.contains(end)
      }
  }

}
