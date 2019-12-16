/*
 * Copyright 2017 Antonio Alonso Dominguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cron4s
package parsing

import cats.syntax.either._

import cron4s.expr._
import cron4s.testkit.Cron4sPropSpec
import cron4s.testkit.gen.{ArbitraryEachNode, NodeGenerators}

import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/**
  * Created by alonsodomin on 13/01/2016.
  */
class ParserSpec
    extends Cron4sPropSpec with ScalaCheckDrivenPropertyChecks with InputGenerators
    with NodeGenerators with ArbitraryEachNode {
  import CronField._
  import CronUnit._
  import CronParser._

  def verifyParsed[F <: CronField, N <: Node[F]](parser: Parser[N], input: String)(
      verify: N => Boolean
  ): Boolean = {
    def readField(tokens: List[CronToken]) = {
      val reader = new CronTokenReader(tokens)
      parser(reader) match {
        case Success(result, _) => verify(result)
        case err: NoSuccess     => false
      }
    }

    CronLexer.tokenize(input).map(readField).getOrElse(false)
  }

  // Utility methods to help with type inference

  def verifyConst[F <: CronField](parser: Parser[ConstNode[F]], input: String)(
      verify: ConstNode[F] => Boolean
  ): Boolean =
    verifyParsed[F, ConstNode[F]](parser, input)(verify)

  def verifyEach(parser: Parser[EachNode[CronField]], input: String): Boolean =
    verifyParsed[CronField, EachNode[CronField]](parser, input)(_ => true)

  def verifyAny(parser: Parser[AnyNode[CronField]], input: String): Boolean =
    verifyParsed[CronField, AnyNode[CronField]](parser, input)(_ => true)

  def verifyBetween[F <: CronField](parser: Parser[BetweenNode[F]], input: String)(
      verify: BetweenNode[F] => Boolean
  ): Boolean =
    verifyParsed[F, BetweenNode[F]](parser, input)(verify)

  def verifySeveral[F <: CronField, A](
      parser: Parser[SeveralNode[F]],
      input: String,
      expected: List[Either[String, (A, A)]]
  )(
      verify: (ConstNode[F], String) => Boolean
  ): Boolean =
    verifyParsed[F, SeveralNode[F]](parser, input) { expr =>
      if (expr.values.toList.size == expected.size) {
        val matches = expr.values.toList.zip(expected).map {
          case (exprPart, expectedPart) =>
            expectedPart match {
              case Left(value) =>
                exprPart.raw.select[ConstNode[F]].exists(verify(_, value))

              case Right((start, end)) =>
                exprPart.raw.select[BetweenNode[F]].exists { part =>
                  verify(part.begin, start.toString) && verify(part.end, end.toString)
                }
            }
        }

        !matches.contains(false)
      } else false
    }

  // --------------------------------------------------------------
  // Properties for the individual parsers
  // --------------------------------------------------------------

  val eachParserGen: Gen[Parser[EachNode[CronField]]] =
    Gen.oneOf(CronUnit.All.map(each(_).asInstanceOf[Parser[EachNode[CronField]]]))

  property("should be able to parse an asterisk in any field") {
    forAll(eachParserGen) { parser =>
      verifyEach(parser, "*")
    }
  }

  val anyParserGen: Gen[Parser[AnyNode[CronField]]] =
    Gen.oneOf(
      Seq(any[DayOfMonth], any[DayOfWeek])
        .map(_.asInstanceOf[Parser[AnyNode[CronField]]])
    )

  property("should be able to parse a question mark in any field") {
    forAll(anyParserGen) { parser =>
      verifyAny(parser, "?")
    }
  }

  property("should be able to parse seconds") {
    forAll(secondsOrMinutesGen) { x =>
      verifyConst(seconds, x) { expr =>
        expr.value == x.toInt
      }
    }
  }

  property("should be able to parse minutes") {
    forAll(secondsOrMinutesGen) { x =>
      verifyConst(minutes, x) { expr =>
        expr.value == x.toInt
      }
    }
  }

  property("should be able to parse hours") {
    forAll(hoursGen) { x =>
      verifyConst(hours, x) { expr =>
        expr.value == x.toInt
      }
    }
  }

  property("should be able to parse days of month") {
    forAll(daysOfMonthGen) { x =>
      verifyConst(daysOfMonth, x) { expr =>
        expr.value == x.toInt
      }
    }
  }

  property("should be able to parse numeric months") {
    forAll(numericMonthsGen) { x =>
      verifyConst(months, x.toString) { expr =>
        expr.value == x.toInt && expr.textValue.isEmpty
      }
    }
  }
  property("should be able to parse named months") {
    forAll(nameMonthsGen) { x =>
      verifyConst(months, x) { expr =>
        expr.textValue.contains(x) && expr.matches(Months.textValues.indexOf(x) + 1)
      }
    }
  }

  property("should be able to parse numeric days of week") {
    forAll(numericDaysOfWeekGen) { x =>
      verifyConst(daysOfWeek, x) { _.value == x.toInt }
    }
  }
  property("should be able to parse named days of week") {
    forAll(namedDaysOfWeekGen) { x =>
      verifyConst(daysOfWeek, x) { expr =>
        expr.textValue.contains(x) && expr.matches(DaysOfWeek.textValues.indexOf(x))
      }
    }
  }

  property("should be able to parse ranges in seconds") {
    forAll(secondsOrMinutesRangeGen) {
      case (input, (start, end)) =>
        verifyBetween(between(seconds), input) { expr =>
          expr.begin.value == start && expr.end.value == end
        }
    }
  }

  property("should be able to parse ranges in minutes") {
    forAll(secondsOrMinutesRangeGen) {
      case (input, (start, end)) =>
        verifyBetween(between(minutes), input) { expr =>
          expr.begin.value == start && expr.end.value == end
        }
    }
  }

  property("should be able to parse ranges in hours") {
    forAll(hoursRangeGen) {
      case (input, (start, end)) =>
        verifyBetween(between(hours), input) { expr =>
          expr.begin.value == start && expr.end.value == end
        }
    }
  }

  property("should be able to parse ranges in days of month") {
    forAll(daysOfMonthRangeGen) {
      case (input, (start, end)) =>
        verifyBetween(between(daysOfMonth), input) { expr =>
          expr.begin.value == start && expr.end.value == end
        }
    }
  }

  property("should be able to parse ranges for numeric months") {
    forAll(numericMonthsRangeGen) {
      case (input, (start, end)) =>
        verifyBetween(between(months), input) { expr =>
          expr.begin.value == start && expr.end.value == end
        }
    }
  }
  property("should be able to parse ranges for named months") {
    forAll(namedMonthsRangeGen) {
      case (input, (start, end)) =>
        verifyBetween(between(months), input) { expr =>
          expr.begin.textValue.contains(start) && expr.end.textValue
            .contains(end)
        }
    }
  }

  property("should be able to parse ranges for numeric days of week") {
    forAll(numericDaysOfWeekRangeGen) {
      case (input, (start, end)) =>
        verifyBetween(between(daysOfWeek), input) { expr =>
          expr.begin.value == start && expr.end.value == end
        }
    }
  }
  property("should be able to parse ranges for named days of week") {
    forAll(namedDaysOfWeekRangeGen) {
      case (input, (start, end)) =>
        verifyBetween(between(daysOfWeek), input) { expr =>
          expr.begin.textValue.contains(start) && expr.end.textValue
            .contains(end)
        }
    }
  }

  property("should be able to parse sequences of second expressions") {
    forAll(secondsOrMinutesSeqGen) {
      case (input, values) =>
        verifySeveral(several(seconds), input, values) { (expr, expected) =>
          expr.value == expected.toInt
        }
    }
  }

  property("should be able to parse sequences of minutes expressions") {
    forAll(secondsOrMinutesSeqGen) {
      case (input, values) =>
        verifySeveral(several(minutes), input, values) { (expr, expected) =>
          expr.value == expected.toInt
        }
    }
  }

  property("should be able to parse sequences of hour expressions") {
    forAll(hoursSeqGen) {
      case (input, values) =>
        verifySeveral(several(hours), input, values) { (expr, expected) =>
          expr.value == expected.toInt
        }
    }
  }

  property("should be able to parse sequences of days of month expressions") {
    forAll(daysOfMonthSeqGen) {
      case (input, values) =>
        verifySeveral(several(daysOfMonth), input, values) { (expr, expected) =>
          expr.value == expected.toInt
        }
    }
  }

  property("should be able to parse sequences of numeric month expressions") {
    forAll(numericMonthsSeqGen) {
      case (input, values) =>
        verifySeveral(several(months), input, values) { (expr, expected) =>
          expr.value == expected.toInt
        }
    }
  }
  property("should be able to parse sequences of named month expressions") {
    forAll(namedMonthsSeqGen) {
      case (input, values) =>
        verifySeveral(several(months), input, values) { (expr, expected) =>
          expr.textValue.contains(expected)
        }
    }
  }

  property("should be able to parse sequences of numeric days of week expressions") {
    forAll(numericDaysOfWeekSeqGen) {
      case (input, values) =>
        verifySeveral(several(daysOfWeek), input, values) { (expr, expected) =>
          expr.value == expected.toInt
        }
    }
  }
  property("should be able to parse sequences of named days of week expressions") {
    forAll(namedDaysOfWeekSeqGen) {
      case (input, values) =>
        verifySeveral(several(daysOfWeek), input, values) { (expr, expected) =>
          expr.textValue.contains(expected)
        }
    }
  }
}
