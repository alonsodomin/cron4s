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

import cron4s.expr._

import scala.util.parsing.input._
import scala.util.parsing.combinator._

class TokenReader(tokens: List[Token]) extends Reader[Token] {
  override def first: Token        = tokens.head
  override def atEnd: Boolean      = tokens.isEmpty
  override def pos: Position       = NoPosition
  override def rest: Reader[Token] = new TokenReader(tokens.tail)
}

object Parser extends Parsers {
  import CronField._
  import CronUnit._
  import Token._

  override type Elem = Token

  private val sexagesimal: Parser[Int] =
    accept("sexagesimal", { case Sexagesimal(s) => s })

  private val decimal: Parser[Int] =
    accept("decimal", { case Decimal(d) => d })

  private val literal: Parser[String] =
    accept("literal", { case Literal(l) => l })

  private val separator: Parser[Char] =
    accept("separator", { case Separator() => ' ' })

  //----------------------------------------
  // Individual Expression Atoms
  //----------------------------------------

  // Seconds

  val seconds: Parser[ConstNode[Second]] =
    sexagesimal.map(ConstNode[Second](_))

  // Minutes

  val minutes: Parser[ConstNode[Minute]] =
    sexagesimal.map(ConstNode[Minute](_))

  // Hours

  val hours: Parser[ConstNode[Hour]] =
    decimal.filter(_ < 24).map(ConstNode[Hour](_))

  // Days Of Month

  val daysOfMonth: Parser[ConstNode[DayOfMonth]] =
    decimal.filter(_ <= 31).map(ConstNode[DayOfMonth](_))

  // Months

  private[this] val numericMonths =
    decimal.filter(_ <= 12).map(ConstNode[Month](_))

  private[this] val textualMonths =
    literal.filter(Months.textValues.contains).map { value =>
      val index = Months.textValues.indexOf(value)
      ConstNode[Month](index + 1, Some(value))
    }

  val months: Parser[ConstNode[Month]] =
    textualMonths | numericMonths

  // Days Of Week

  private[this] val numericDaysOfWeek =
    decimal.filter(_ < 7).map(ConstNode[DayOfWeek](_))

  private[this] val textualDaysOfWeek =
    literal.filter(DaysOfWeek.textValues.contains).map { value =>
      val index = DaysOfWeek.textValues.indexOf(value)
      ConstNode[DayOfWeek](index, Some(value))
    }

  val daysOfWeek: Parser[ConstNode[DayOfWeek]] =
    textualDaysOfWeek | numericDaysOfWeek

  //----------------------------------------
  // Field-Based Expression Atoms
  //----------------------------------------

  def each[F <: CronField](implicit unit: CronUnit[F]): Parser[EachNode[F]] =
    accept("each", { case Star() => EachNode[F] })

  def any[F <: CronField](implicit unit: CronUnit[F]): Parser[AnyNode[F]] =
    accept("any", { case Question() => AnyNode[F] })

  def between[F <: CronField](base: Parser[ConstNode[F]])(
      implicit unit: CronUnit[F]
  ): Parser[BetweenNode[F]] =
    ((base <~ Hyphen()) ~ base) ^^ { case min ~ max => BetweenNode[F](min, max) }

  def several[F <: CronField](base: Parser[ConstNode[F]])(
      implicit unit: CronUnit[F]
  ): Parser[SeveralNode[F]] = {
    def compose(b: Parser[EnumerableNode[F]]) =
      repsep(b, Comma())
        .filter(_.length > 1)
        .map(values => SeveralNode.fromSeq[F](values).get)

    compose(between(base).map(between2Enumerable) | base.map(const2Enumerable))
  }

  def every[F <: CronField](base: Parser[ConstNode[F]])(
      implicit unit: CronUnit[F]
  ): Parser[EveryNode[F]] = {
    def compose(b: Parser[DivisibleNode[F]]) =
      ((b <~ Slash()) ~ decimal) ^^ {
        case exp ~ freq => EveryNode[F](exp, freq)
      }

    compose(
      several(base).map(several2Divisible) |
        between(base).map(between2Divisible) |
        each[F].map(each2Divisible)
    )
  }

  //----------------------------------------
  // AST Parsing & Building
  //----------------------------------------

  def field[F <: CronField](base: Parser[ConstNode[F]])(
      implicit unit: CronUnit[F]
  ): Parser[FieldNode[F]] =
    every(base).map(every2Field) |
      several(base).map(several2Field) |
      between(base).map(between2Field) |
      base.map(const2Field) |
      each[F].map(each2Field)

  def fieldWithAny[F <: CronField](base: Parser[ConstNode[F]])(
      implicit unit: CronUnit[F]
  ): Parser[FieldNodeWithAny[F]] =
    every(base).map(every2FieldWithAny) |
      several(base).map(several2FieldWithAny) |
      between(base).map(between2FieldWithAny) |
      base.map(const2FieldWithAny) |
      each[F].map(each2FieldWithAny) |
      any[F].map(any2FieldWithAny)

  val cron: Parser[CronExpr] = {
    (field(seconds) <~ separator) ~
      (field(minutes) <~ separator) ~
      (field(hours) <~ separator) ~
      (fieldWithAny(daysOfMonth) <~ separator) ~
      (field(months) <~ separator) ~
      fieldWithAny(daysOfWeek) ^^ {
      case sec ~ min ~ hour ~ day ~ month ~ weekDay =>
        CronExpr(sec, min, hour, day, month, weekDay)
    }
  }

  def apply(tokens: List[Token]): Either[LexerError, CronExpr] = {
    val reader = new TokenReader(tokens)
    cron(reader) match {
      case NoSuccess(msg, _) => Left(LexerError(msg, -1))
      case Success(expr, _)  => Right(expr)
    }
  }

}
