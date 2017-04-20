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

import cats.data.NonEmptyList

import cron4s.expr._

import fastparse.all._

import shapeless._

/**
  * Created by alonsodomin on 15/12/2016.
  */
package object parser extends NodeConversions {
  import CronField._
  import CronUnit._

  private[this] val digit = P(CharIn('0' to '9'))
  private[this] val sexagesimal = P(((CharIn('1' to '5') ~ digit) | ("0" ~ digit) | digit).!).map(_.toInt)

  //----------------------------------------
  // Individual Expression Atoms
  //----------------------------------------

  // Seconds

  val seconds: Parser[ConstNode[Second]] = sexagesimal.map(value => ConstNode[Second](value))

  // Minutes

  val minutes: Parser[ConstNode[Minute]] = sexagesimal.map(value => ConstNode[Minute](value))

  // Hours

  val hours: Parser[ConstNode[Hour]] = P((("2" ~ CharIn('0' to '3')) | ("0" | "1") ~ digit | digit).!).map {
    value => ConstNode[Hour](value.toInt)
  }

  // Day Of Month

  val daysOfMonth: Parser[ConstNode[DayOfMonth]] = P(((("0" | "1" | "2") ~ digit) | ("3" ~ ("0" | "1")) | digit).!).map {
    value => ConstNode[DayOfMonth](value.toInt)
  }

  // Month

  private[this] val numericMonth = P((("1" ~ CharIn('0' to '2')) | ("0".? ~ digit)).!).map {
    value => ConstNode[Month](value.toInt)
  }.opaque("numeric month")
  private[this] val textualMonth = P(StringIn(Months.textValues: _*).!).map { value =>
    val index = Months.textValues.indexOf(value)
    ConstNode[Month](index + 1, Some(value))
  }

  val months: Parser[ConstNode[Month]] = textualMonth | numericMonth

  // Day Of Week

  private[this] val numericDayOfWeek = P(CharIn('0' to '6').!)
    .map(value => ConstNode[DayOfWeek](value.toInt))
  private[this] val textualDayOfWeek = P(StringIn(DaysOfWeek.textValues: _*).!).map { value =>
    val index = DaysOfWeek.textValues.indexOf(value)
    ConstNode[DayOfWeek](index, Some(value))
  }
  val daysOfWeek: Parser[ConstNode[DayOfWeek]] = numericDayOfWeek | textualDayOfWeek

  //----------------------------------------
  // Field-Based Expression Atoms
  //----------------------------------------

  def each[F <: CronField](implicit unit: CronUnit[F]): Parser[EachNode[F]] =
    P("*").map(_ => EachNode[F])

  def any[F <: CronField](implicit unit: CronUnit[F]): Parser[AnyNode[F]] =
    P("?").map(_ => AnyNode[F])

  def between[F <: CronField](p: Parser[ConstNode[F]])(implicit unit: CronUnit[F]): Parser[BetweenNode[F]] =
    (p ~ "-" ~ p).map { case (min, max) => BetweenNode[F](min, max) }

  def several[F <: CronField](p: Parser[ConstNode[F]])(implicit unit: CronUnit[F]): Parser[SeveralNode[F]] = {
    def compose(p: Parser[EnumerableNode[F]])(implicit unit: CronUnit[F]): Parser[SeveralNode[F]] =
      p.rep(min = 2, sep = ",")
        .map(values => SeveralNode[F](values.head, values.tail: _*))

    compose(between(p).map(between2Enumerable) | p.map(const2Enumerable))
  }

  def every[F <: CronField](p: Parser[ConstNode[F]])(implicit unit: CronUnit[F]): Parser[EveryNode[F]] = {
    def compose(p: Parser[DivisibleNode[F]])(implicit unit: CronUnit[F]): Parser[EveryNode[F]] =
      (p ~ "/" ~/ digit.rep(1).!).map { case (base, freq) => EveryNode[F](base, freq.toInt) }

    compose(
      several(p).map(several2Divisible) |
      between(p).map(between2Divisible) |
      each[F].map(each2Divisible)
    )
  }

  //----------------------------------------
  // AST Parsing & Building
  //----------------------------------------

  def of[F <: CronField](p: Parser[ConstNode[F]])(implicit unit: CronUnit[F]): Parser[FieldNode[F]] = {
    every(p).map(every2Field) |
      several(p).map(several2Field) |
      between(p).map(between2Field) |
      p.map(const2Field) |
      each[F].map(each2Field)
  }

  def withAny[F <: CronField](p: Parser[ConstNode[F]])(implicit unit: CronUnit[F]): Parser[FieldNodeWithAny[F]] = {
    every(p).map(every2FieldWithAny) |
      several(p).map(several2FieldWithAny) |
      between(p).map(between2FieldWithAny) |
      p.map(const2FieldWithAny) |
      each[F].map(each2FieldWithAny) |
      any[F].map(any2FieldWithAny)
  }

  val cron: Parser[CronExpr] = P(
    Start ~
      (of(seconds) ~ " ").opaque("0-59") ~/
      (of(minutes) ~ " ").opaque("0-59") ~/
      (of(hours) ~ " ").opaque("0-23") ~/
      (withAny(daysOfMonth) ~ " ").opaque("1-31") ~/
      (of(months) ~ " ").opaque("<month>") ~/
      withAny(daysOfWeek).opaque("<day-of-week>") ~/
    End
  ).map { case (sec, min, hour, day, month, weekDay) =>
    CronExpr(sec, min, hour, day, month, weekDay)
  }

}
