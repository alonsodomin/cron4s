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

package cron4s.atto
import _root_.atto.{Parser => AttoParser, _}
import atto.Atto._
import cats.implicits._
import cron4s.parser.Node._
import cron4s.parser._

import java.util.Locale

private[atto] trait CronExprParser {
  private[atto] def int = digit.many1.flatMap { n =>
    try
      ok(n.toList.mkString.toInt)
    catch {
      case e: NumberFormatException => err[Int](s"${n} is not valid int")
    }
  }
  private[atto] def oneOrTwoDigitsPositiveInt: AttoParser[Int] = {

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

  private[atto] def NDigitsPositiveInt(n: Int): AttoParser[Int] = {

    val getDigits = digit.manyN(n).map(_.mkString)

    getDigits.flatMap(s =>
      try
        ok(s.toInt)
      catch {
        // scala-js can't parse non-alpha digits so we just fail in that case.
        case _: java.lang.NumberFormatException =>
          err[Int]("https://github.com/scala-js/scala-js/issues/2935")
      }
    )
  } namedOpaque s"${n}DigitsPositiveInt"

  private[atto] val sexagesimal: AttoParser[Int] =
    oneOrTwoDigitsPositiveInt.filter(x => x >= 0 && x < 60)

  private[atto] val literal: AttoParser[String] = takeWhile1(x => x != ' ' && x != '-' && x != ',')

  private[atto] val hyphen: AttoParser[Char]       = elem(_ == '-', "hyphen")
  private[atto] val comma: AttoParser[Char]        = elem(_ == ',', "comma")
  private[atto] val slash: AttoParser[Char]        = elem(_ == '/', "slash")
  private[atto] val asterisk: AttoParser[Char]     = elem(_ == '*', "asterisk")
  private[atto] val questionMark: AttoParser[Char] = elem(_ == '?', "question-mark")
  private[atto] val blank: AttoParser[Char]        = elem(_ == ' ', "blank")

  // ----------------------------------------
  // Individual Expression Atoms
  // ----------------------------------------

  // Seconds
  private[atto] val seconds: AttoParser[ConstNode] = sexagesimal.map(ConstNode(_))

  // Minutes

  private[atto] val minutes: AttoParser[ConstNode] = sexagesimal.map(ConstNode(_))

  // Hours

  private[atto] val hours: AttoParser[ConstNode] =
    oneOrTwoDigitsPositiveInt.filter(x => (x >= 0) && (x < 24)).map(ConstNode(_))

  // Days Of Month

  private[atto] val daysOfMonth: AttoParser[ConstNode] =
    oneOrTwoDigitsPositiveInt.filter(x => (x >= 1) && (x <= 31)).map(ConstNode(_))

  // Months

  private[this] val numericMonths: AttoParser[ConstNode] =
    oneOrTwoDigitsPositiveInt.filter(x => (x >= 0) && (x <= 12)).map(ConstNode(_))

  private[this] val textualMonths: AttoParser[ConstNode] =
    literal.filter(Months.textValues.contains).map { value =>
      val index = Months.textValues.indexOf(value)
      ConstNode(index + 1, Some(value))
    }

  private[atto] val months: AttoParser[ConstNode] =
    textualMonths | numericMonths

  // Days Of Week

  private[this] val numericDaysOfWeek: AttoParser[ConstNode] =
    oneOrTwoDigitsPositiveInt.filter(x => (x >= 0) && (x <= 6)).map(ConstNode(_))

  private[this] val textualDaysOfWeek: AttoParser[ConstNode] =
    literal.map(_.toLowerCase(Locale.US)).filter(DaysOfWeek.textValues.contains).map { value =>
      val index = DaysOfWeek.textValues.indexOf(value)
      ConstNode(index, Some(value))
    }

  private[atto] val daysOfWeek: AttoParser[ConstNode] =
    textualDaysOfWeek | numericDaysOfWeek

  // ----------------------------------------
  // Field-Based Expression Atoms
  // ----------------------------------------

  private[atto] def each: AttoParser[EachNode.type] = asterisk.as(EachNode)

  private[atto] def any: AttoParser[AnyNode.type] = questionMark.as(AnyNode)

  private[atto] def between(base: AttoParser[ConstNode]): AttoParser[BetweenNode] =
    for {
      min <- base <~ hyphen
      max <- base
    } yield BetweenNode(min, max)

  private[atto] def several(base: AttoParser[ConstNode]): AttoParser[SeveralNode] = {
    def compose(b: => AttoParser[EnumerableNode]) =
      sepBy(b, comma)
        .collect {
          case first :: second :: tail => SeveralNode(first, second, tail: _*)
        }

    compose(between(base) | base)
  }

  private[atto] def every(base: AttoParser[ConstNode]): AttoParser[EveryNode] = {
    def compose(b: => AttoParser[DivisibleNode]) =
      ((b <~ slash) ~ oneOrTwoDigitsPositiveInt.filter(_ > 0)).map {
        case (exp, freq) => EveryNode(exp, freq)
      }
    compose(several(base) | between(base) | each)
  }
}
