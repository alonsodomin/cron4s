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

import cron4s.parser._

import scala.util.parsing.input._
import scala.util.parsing.combinator._

class CronTokenReader(tokens: List[CronToken]) extends Reader[CronToken] {
  override def first: CronToken        = tokens.head
  override def atEnd: Boolean          = tokens.isEmpty
  override def pos: Position           = tokens.headOption.map(_.pos).getOrElse(NoPosition)
  override def rest: Reader[CronToken] = new CronTokenReader(tokens.tail)
  override def toString(): String      = tokens.toString()
}

object CronParser extends Parsers with BaseParser {
  import Node._
  import CronToken._

  override type Elem = CronToken

  private val sexagesimal: Parser[Int] =
    accept("sexagesimal", { case Number(s) if s >= 0 && s < 60 => s })

  private val decimal: Parser[Int] =
    accept("decimal", { case Number(d) if d >= 0 => d })

  private val literal: Parser[String] =
    accept("literal", { case Text(l) => l })

  private val blank: Parser[Char] =
    accept("blank", { case Blank => ' ' })

  // ----------------------------------------
  // Individual Expression Atoms
  // ----------------------------------------

  // Seconds

  val seconds: Parser[ConstNode] = sexagesimal.map(ConstNode(_))

  // Minutes

  val minutes: Parser[ConstNode] = sexagesimal.map(ConstNode(_))

  // Hours

  val hours: Parser[ConstNode] = decimal.filter(x => (x >= 0) && (x < 24)).map(ConstNode(_))

  // Days Of Month

  val daysOfMonth: Parser[ConstNode] = decimal.filter(x => (x >= 1) && (x <= 31)).map(ConstNode(_))

  // Months

  private[this] val numericMonths = decimal.filter(_ <= 12).map(ConstNode(_))

  private[this] val textualMonths =
    literal.filter(Months.textValues.contains).map { value =>
      val index = Months.textValues.indexOf(value)
      ConstNode(index + 1, Some(value))
    }

  val months: Parser[ConstNode] =
    textualMonths | numericMonths

  // Days Of Week

  private[this] val numericDaysOfWeek =
    decimal.filter(_ < 7).map(ConstNode(_))

  private[this] val textualDaysOfWeek =
    literal.filter(DaysOfWeek.textValues.contains).map { value =>
      val index = DaysOfWeek.textValues.indexOf(value)
      ConstNode(index, Some(value))
    }

  val daysOfWeek: Parser[ConstNode] =
    textualDaysOfWeek | numericDaysOfWeek

  // ----------------------------------------
  // Field-Based Expression Atoms
  // ----------------------------------------

  def each: Parser[EachNode.type] = accept("*", { case Asterisk => EachNode })

  def any: Parser[AnyNode.type] = accept("?", { case QuestionMark => AnyNode })

  def between(base: Parser[ConstNode]): Parser[BetweenNode] =
    ((base <~ Hyphen) ~ base) ^^ { case min ~ max => BetweenNode(min, max) }

  def several(base: Parser[ConstNode]): Parser[SeveralNode] = {
    def compose(b: Parser[EnumerableNode]) =
      repsep(b, Comma)
        .filter(_.length > 1)
        .map(values => SeveralNode.fromSeq(values).get)

    compose(between(base) | base)
  }

  def every(base: Parser[ConstNode]): Parser[EveryNode] = {
    def compose(b: Parser[DivisibleNode]) =
      ((b <~ Slash) ~ decimal.filter(_ > 0)) ^^ {
        case exp ~ freq => EveryNode(exp, freq)
      }

    compose(several(base) | between(base) | each)
  }

  // ----------------------------------------
  // AST Parsing & Building
  // ----------------------------------------

  def field(base: Parser[ConstNode]): Parser[NodeWithoutAny] =
    every(base) |
      several(base) |
      between(base) |
      base |
      each

  def fieldWithAny(base: Parser[ConstNode]): Parser[Node] =
    every(base) |
      several(base) |
      between(base) |
      base |
      each |
      any

  val cron: Parser[CronExpr] = {
    phrase(
      (field(seconds) <~! blank) ~!
        (field(minutes) <~! blank) ~!
        (field(hours) <~! blank) ~!
        (fieldWithAny(daysOfMonth) <~! blank) ~!
        (field(months) <~! blank) ~!
        (fieldWithAny(daysOfWeek))
    ) ^^ {
      case sec ~ min ~ hour ~ day ~ month ~ weekDay =>
        CronExpr(sec, min, hour, day, month, weekDay)
    }
  }

  def read(tokens: List[CronToken]): Either[_root_.cron4s.parser.Error, CronExpr] = {
    val reader = new CronTokenReader(tokens)
    cron(reader) match {
      case err: NoSuccess   => Left(handleError(err))
      case Success(expr, _) => Right(expr)
    }
  }
}
