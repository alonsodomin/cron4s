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

import _root_.atto._
import Atto._
import cats.implicits._
import cron4s.expr._

package object atto {
  import CronField._
  import CronUnit._

  private def oneOrTwoDigitsPositiveInt: Parser[Int] = {

    val getDigits = for {
      d1 <- digit
      d2 <- opt(digit)
    } yield d2.fold(s"$d1")(x => s"$d1$x")

    getDigits.flatMap(s =>
      try
        ok(s.toInt)
      catch {
        // scala-js can't parse non-alpha digits so we just fail in that case.
        case _: java.lang.NumberFormatException =>
          err[Int]("https://github.com/scala-js/scala-js/issues/2935")
      }
    )
  } namedOpaque "oneOrTwoDigitsPositiveInt"

  private val sexagesimal: Parser[Int] = oneOrTwoDigitsPositiveInt.filter(x => x >= 0 && x < 60)

  private val literal: Parser[String] = takeWhile1(x => x != ' ' && x != '-')

  private val hyphen: Parser[Char]       = elem(_ == '-', "hyphen")
  private val comma: Parser[Char]        = elem(_ == ',', "comma")
  private val slash: Parser[Char]        = elem(_ == '/', "slash")
  private val asterisk: Parser[Char]     = elem(_ == '*', "asterisk")
  private val questionMark: Parser[Char] = elem(_ == '?', "question-mark")
  private val blank: Parser[Char]        = elem(_ == ' ', "blank")

  // ----------------------------------------
  // Individual Expression Atoms
  // ----------------------------------------

  // Seconds

  val seconds: Parser[ConstNode[Second]] =
    sexagesimal.map(ConstNode[Second](_))

  // Minutes

  val minutes: Parser[ConstNode[Minute]] =
    sexagesimal.map(ConstNode[Minute](_))

  // Hours

  val hours: Parser[ConstNode[Hour]] =
    oneOrTwoDigitsPositiveInt.filter(x => (x >= 0) && (x < 24)).map(ConstNode[Hour](_))

  // Days Of Month

  val daysOfMonth: Parser[ConstNode[DayOfMonth]] =
    oneOrTwoDigitsPositiveInt.filter(x => (x >= 1) && (x <= 31)).map(ConstNode[DayOfMonth](_))

  // Months

  private[this] val numericMonths =
    oneOrTwoDigitsPositiveInt.filter(x => (x >= 0) && (x <= 12)).map(ConstNode[Month](_))

  private[this] val textualMonths =
    literal.filter(Months.textValues.contains).map { value =>
      val index = Months.textValues.indexOf(value)
      ConstNode[Month](index + 1, Some(value))
    }

  val months: Parser[ConstNode[Month]] =
    textualMonths | numericMonths

  // Days Of Week

  private[this] val numericDaysOfWeek =
    oneOrTwoDigitsPositiveInt.filter(x => (x >= 0) && (x <= 6)).map(ConstNode[DayOfWeek](_))

  private[this] val textualDaysOfWeek =
    literal.filter(DaysOfWeek.textValues.contains).map { value =>
      val index = DaysOfWeek.textValues.indexOf(value)
      ConstNode[DayOfWeek](index, Some(value))
    }

  val daysOfWeek: Parser[ConstNode[DayOfWeek]] =
    textualDaysOfWeek | numericDaysOfWeek

  // ----------------------------------------
  // Field-Based Expression Atoms
  // ----------------------------------------

  def each[F <: CronField](implicit unit: CronUnit[F]): Parser[EachNode[F]] =
    asterisk.as(EachNode[F])

  def any[F <: CronField](implicit unit: CronUnit[F]): Parser[AnyNode[F]] =
    questionMark.as(AnyNode[F])

  def between[F <: CronField](base: Parser[ConstNode[F]])(implicit
      unit: CronUnit[F]
  ): Parser[BetweenNode[F]] =
    for {
      min <- base <~ hyphen
      max <- base
    } yield BetweenNode[F](min, max)

  def several[F <: CronField](base: Parser[ConstNode[F]])(implicit
      unit: CronUnit[F]
  ): Parser[SeveralNode[F]] = {
    def compose(b: => Parser[EnumerableNode[F]]) =
      sepBy(b, comma)
        .collect {
          case first :: second :: tail => SeveralNode(first, second, tail: _*)
        }

    compose(between(base).map(between2Enumerable) | base.map(const2Enumerable))
  }

  def every[F <: CronField](base: Parser[ConstNode[F]])(implicit
      unit: CronUnit[F]
  ): Parser[EveryNode[F]] = {
    def compose(b: => Parser[DivisibleNode[F]]) =
      ((b <~ slash) ~ oneOrTwoDigitsPositiveInt.filter(_ > 0)).map {
        case (exp, freq) => EveryNode[F](exp, freq)
      }

    compose(
      several(base).map(several2Divisible) |
        between(base).map(between2Divisible) |
        each[F].map(each2Divisible)
    )
  }

  // ----------------------------------------
  // AST Parsing & Building
  // ----------------------------------------

  def field[F <: CronField](base: Parser[ConstNode[F]])(implicit
      unit: CronUnit[F]
  ): Parser[FieldNode[F]] =
    every(base).map(every2Field) |
      several(base).map(several2Field) |
      between(base).map(between2Field) |
      base.map(const2Field) |
      each[F].map(each2Field)

  def fieldWithAny[F <: CronField](base: Parser[ConstNode[F]])(implicit
      unit: CronUnit[F]
  ): Parser[FieldNodeWithAny[F]] =
    every(base).map(every2FieldWithAny) |
      several(base).map(several2FieldWithAny) |
      between(base).map(between2FieldWithAny) |
      base.map(const2FieldWithAny) |
      each[F].map(each2FieldWithAny) |
      any[F].map(any2FieldWithAny)

  val cron: Parser[CronExpr] = for {
    sec     <- field(seconds) <~ blank
    min     <- field(minutes) <~ blank
    hour    <- field(hours) <~ blank
    day     <- fieldWithAny(daysOfMonth) <~ blank
    month   <- field(months) <~ blank
    weekDay <- fieldWithAny(daysOfWeek)
  } yield CronExpr(sec, min, hour, day, month, weekDay)

  def parse(e: String): Either[Error, CronExpr] =
    (phrase(cron).parseOnly(e): @unchecked) match {
      case ParseResult.Done(_, result) => Right(result)
      case ParseResult.Fail("", _, _)  => Left(ExprTooShort)
      case ParseResult.Fail(rest, _, msg) =>
        val position = e.length() - rest.length() + 1
        Left(ParseFailed(msg, position, Some(rest)))
    }
}
