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

import cron4s.expr.CronExpr
import cron4s.expr.ast._

import scala.util.parsing.input._
import scala.util.parsing.combinator._

class CronTokenReader(tokens: List[CronToken]) extends Reader[CronToken] {
  override def first: CronToken        = tokens.head
  override def atEnd: Boolean          = tokens.isEmpty
  override def pos: Position           = tokens.headOption.map(_.pos).getOrElse(NoPosition)
  override def rest: Reader[CronToken] = new CronTokenReader(tokens.tail)
}

object CronParser extends Parsers with BaseParser {
  import CronField._
  import CronUnit._
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

  //----------------------------------------
  // Individual Expression Atoms
  //----------------------------------------

  // Seconds

  val seconds: Parser[ConstValue[Second]] =
    sexagesimal.map(ConstValue[Second](_, None, CronUnit[Second]))

  // Minutes

  val minutes: Parser[ConstValue[Minute]] =
    sexagesimal.map(ConstValue[Minute](_, None, CronUnit[Minute]))

  // Hours

  val hours: Parser[ConstValue[Hour]] =
    decimal
      .filter(x => (x >= 0) && (x < 24))
      .map(ConstValue[Hour](_, None, CronUnit[Hour]))

  // Days Of Month

  val daysOfMonth: Parser[ConstValue[DayOfMonth]] =
    decimal
      .filter(x => (x >= 1) && (x <= 31))
      .map(ConstValue[DayOfMonth](_, None, CronUnit[DayOfMonth]))

  // Months

  private[this] val numericMonths =
    decimal.filter(_ <= 12).map(ConstValue[Month](_, None, CronUnit[Month]))

  private[this] val textualMonths =
    literal.filter(Months.textValues.contains).map { value =>
      val index = Months.textValues.indexOf(value)
      ConstValue[Month](index + 1, Some(value), CronUnit[Month])
    }

  val months: Parser[ConstValue[Month]] =
    textualMonths | numericMonths

  // Days Of Week

  private[this] val numericDaysOfWeek =
    decimal.filter(_ < 7).map(ConstValue[DayOfWeek](_, None, CronUnit[DayOfWeek]))

  private[this] val textualDaysOfWeek =
    literal.filter(DaysOfWeek.textValues.contains).map { value =>
      val index = DaysOfWeek.textValues.indexOf(value)
      ConstValue[DayOfWeek](index, Some(value), CronUnit[DayOfWeek])
    }

  val daysOfWeek: Parser[ConstValue[DayOfWeek]] =
    textualDaysOfWeek | numericDaysOfWeek

  //----------------------------------------
  // Range-Based Expression Atoms
  //----------------------------------------

  def each[F <: CronField](implicit unit: CronUnit[F]): Parser[EachInRange[F]] =
    accept("*", { case Asterisk => EachInRange[F](unit) })

  def any[F <: CronField](implicit unit: CronUnit[F]): Parser[AnyInRange[F]] =
    accept("?", { case QuestionMark => AnyInRange[F](unit) })

  def between[F <: CronField](base: Parser[ConstValue[F]])(
      implicit unit: CronUnit[F]
  ): Parser[BoundedRange[F]] =
    ((base <~ Hyphen) ~ base) ^^ { case min ~ max => BoundedRange[F](min, max, unit) }

  def several[F <: CronField](base: Parser[ConstValue[F]])(
      implicit unit: CronUnit[F]
  ): Parser[EnumeratedRange[F]] = {
    def compose(b: Parser[ComposableRange[F]]) =
      repsep(b, Comma)
        .filter(_.length > 1)
        .map(values => EnumeratedRange.fromList[F](values).get)

    compose(between(base) | base)
  }

  def every[F <: CronField](base: Parser[ConstValue[F]])(
      implicit unit: CronUnit[F]
  ): Parser[SteppingRange[F]] = {
    def compose(b: Parser[DivisibleRange[F]]) =
      ((b <~ Slash) ~ decimal.filter(_ > 0)) ^^ {
        case exp ~ freq => SteppingRange[F](exp, freq, unit)
      }

    compose(
      several(base) |
        between(base) |
        each[F]
    )
  }

  def range[F <: CronField](base: Parser[ConstValue[F]])(
      implicit unit: CronUnit[F]
  ): Parser[CronRange[F]] =
    (every(base) | several(base) | between(base) | base | each[F])

  //----------------------------------------
  // AST Parsing & Building
  //----------------------------------------

  def node[F <: CronField](implicit unit: CronUnit[F]): Parser[CronNode[F]] = {
    val rangeParser: Parser[CronRange[F]] = unit.field match {
      case Second     => range(seconds).asInstanceOf[Parser[CronRange[F]]]
      case Minute     => range(minutes).asInstanceOf[Parser[CronRange[F]]]
      case Hour       => range(hours).asInstanceOf[Parser[CronRange[F]]]
      case DayOfMonth => range(daysOfMonth).asInstanceOf[Parser[CronRange[F]]]
      case Month      => range(months).asInstanceOf[Parser[CronRange[F]]]
      case DayOfWeek  => range(daysOfWeek).asInstanceOf[Parser[CronRange[F]]]
    }
    rangeParser.map(RangeNode[F])
  }

  // def field[F <: CronField](base: Parser[ConstNode[F]])(
  //     implicit unit: CronUnit[F]
  // ): Parser[FieldNode[F]] =
  //   every(base).map(every2Field) |
  //     several(base).map(several2Field) |
  //     between(base).map(between2Field) |
  //     base.map(const2Field) |
  //     each[F].map(each2Field)

  // def fieldWithAny[F <: CronField](base: Parser[ConstNode[F]])(
  //     implicit unit: CronUnit[F]
  // ): Parser[FieldNodeWithAny[F]] =
  //   every(base).map(every2FieldWithAny) |
  //     several(base).map(several2FieldWithAny) |
  //     between(base).map(between2FieldWithAny) |
  //     base.map(const2FieldWithAny) |
  //     each[F].map(each2FieldWithAny) |
  //     any[F].map(any2FieldWithAny)

  val cron: Parser[CronExpr] = {
    (node[Second] <~ blank) ~
      (node[Minute] <~ blank) ~
      (node[Hour] <~ blank) ~
      (node[DayOfMonth] <~ blank) ~
      (node[Month] <~ blank) ~
      node[DayOfWeek] ^^ {
      case sec ~ min ~ hour ~ day ~ month ~ weekDay =>
        CronExpr(sec, min, hour, day, month, weekDay)
    }
  }

  def read(tokens: List[CronToken]): Either[_root_.cron4s.Error, CronExpr] = {
    val reader = new CronTokenReader(tokens)
    cron(reader) match {
      case err: NoSuccess   => Left(handleError(err))
      case Success(expr, _) => Right(expr)
    }
  }

}
