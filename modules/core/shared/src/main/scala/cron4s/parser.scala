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

import cron4s.expr._

import fastparse.all._

private[cron4s] object parser {

  import CronField._
  import CronUnit._

  private[this] val digit = P(CharIn('0' to '9'))
  private[this] val sexagesimal =
    P(((CharIn('1' to '5') ~ digit) | ("0" ~ digit) | digit).!)
      .map(_.toInt)
      .opaque("0-59")

  //----------------------------------------
  // Individual Expression Atoms
  //----------------------------------------

  // Seconds

  val seconds: Parser[ConstNode[Second]] =
    sexagesimal.map(value => ConstNode[Second](value))

  // Minutes

  val minutes: Parser[ConstNode[Minute]] =
    sexagesimal.map(value => ConstNode[Minute](value))

  // Hours

  val hours: Parser[ConstNode[Hour]] =
    P((("2" ~ CharIn('0' to '3')) | ("0" | "1") ~ digit | digit).!).map { value =>
      ConstNode[Hour](value.toInt)
    }

  // Day Of Month

  val daysOfMonth: Parser[ConstNode[DayOfMonth]] =
    P(((("0" | "1" | "2") ~ digit) | ("3" ~ ("0" | "1")) | digit).!).map { value =>
      ConstNode[DayOfMonth](value.toInt)
    }

  // Month

  private[this] val numericMonth =
    P((("1" ~ CharIn('0' to '2')) | ("0".? ~ digit)).!)
      .map { value =>
        ConstNode[Month](value.toInt)
      }
      .opaque("numeric month")
  private[this] val textualMonth = P(StringIn(Months.textValues: _*).!).map { value =>
    val index = Months.textValues.indexOf(value)
    ConstNode[Month](index + 1, Some(value))
  }

  val months: Parser[ConstNode[Month]] = textualMonth | numericMonth

  // Day Of Week

  private[this] val numericDayOfWeek = P(CharIn('0' to '6').!)
    .map(value => ConstNode[DayOfWeek](value.toInt))
  private[this] val textualDayOfWeek =
    P(StringIn(DaysOfWeek.textValues: _*).!).map { value =>
      val index = DaysOfWeek.textValues.indexOf(value)
      ConstNode[DayOfWeek](index, Some(value))
    }
  val daysOfWeek: Parser[ConstNode[DayOfWeek]] = numericDayOfWeek | textualDayOfWeek

  //----------------------------------------
  // Field-Based Expression Atoms
  //----------------------------------------

  def each[F <: CronField](implicit unit: CronUnit[F]): Parser[EachNode[F]] =
    P("*" ~ &(" " | "/" | End)).map(_ => EachNode[F])

  def any[F <: CronField](implicit unit: CronUnit[F]): Parser[AnyNode[F]] =
    P("?" ~ &(" " | End)).map(_ => AnyNode[F])

  def between[F <: CronField](p: Parser[ConstNode[F]], lookAhead: Boolean = true)(
      implicit unit: CronUnit[F]
  ): Parser[BetweenNode[F]] = {
    val range = p ~ "-" ~ p
    val parser = if (lookAhead) {
      range ~ &(" " | "/" | End)
    } else range

    parser.map { case (min, max) => BetweenNode[F](min, max) }
  }

  def several[F <: CronField](p: Parser[ConstNode[F]])(
      implicit unit: CronUnit[F]
  ): Parser[SeveralNode[F]] = {
    def compose(p: Parser[EnumerableNode[F]]): Parser[SeveralNode[F]] =
      p.rep(min = 2, sep = ",").map { values =>
        SeveralNode.fromSeq(values).get
      }

    compose(between(p, lookAhead = false).map(between2Enumerable) | p.map(const2Enumerable))
  }

  def every[F <: CronField](p: Parser[ConstNode[F]])(
      implicit unit: CronUnit[F]
  ): Parser[EveryNode[F]] = {
    def compose(p: Parser[DivisibleNode[F]]): Parser[EveryNode[F]] =
      (p ~ "/" ~/ digit.rep(1).!).map {
        case (base, freq) => EveryNode[F](base, freq.toInt)
      }

    compose(
      each[F].map(each2Divisible) | between(p)
        .map(between2Divisible) | several(p).map(several2Divisible)
    )
  }

  //----------------------------------------
  // AST Parsing & Building
  //----------------------------------------

  def of[F <: CronField](
      p: Parser[ConstNode[F]]
  )(implicit unit: CronUnit[F]): Parser[FieldNode[F]] =
    every(p).map(every2Field) | between(p).map(between2Field) | several(p).map(several2Field) |
      p.map(const2Field) |
      each[F].map(each2Field)

  def withAny[F <: CronField](
      p: Parser[ConstNode[F]]
  )(implicit unit: CronUnit[F]): Parser[FieldNodeWithAny[F]] =
    every(p).map(every2FieldWithAny) |
      between(p).map(between2FieldWithAny) |
      several(p).map(several2FieldWithAny) |
      p.map(const2FieldWithAny) |
      each[F].map(each2FieldWithAny) |
      any[F].map(any2FieldWithAny)

  val cron: Parser[CronExpr] = P(
    Start ~
      (of(seconds) ~ " ") ~/
      (of(minutes) ~ " ") ~/
      (of(hours) ~ " ") ~/
      (withAny(daysOfMonth) ~ " ") ~/
      (of(months) ~ " ") ~/
      withAny(daysOfWeek) ~/
      End
  ).map {
    case (sec, min, hour, day, month, weekDay) =>
      CronExpr(sec, min, hour, day, month, weekDay)
  }

}
