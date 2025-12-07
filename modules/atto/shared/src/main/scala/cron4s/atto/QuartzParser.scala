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

trait QuartzParser extends cron4s.parser.Parser with CronExprParser {

  import cron4s.parser._
  import cron4s.parser.Node._
  private[atto] val year: AttoParser[ConstNode] = int.map(ConstNode(_))

  // ----------------------------------------
  // AST Parsing & Building
  // ----------------------------------------

  private def field(base: AttoParser[ConstNode]): AttoParser[NodeWithoutAny] =
    every(base) |
      several(base) |
      between(base) |
      base |
      each

  private def fieldWithAny(base: AttoParser[ConstNode]): AttoParser[Node] =
    every(base) |
      several(base) |
      between(base) |
      base |
      each |
      any

  private def fieldForYear: AttoParser[NodeWithoutAny] = yearsEvery(year) |
    several(year) |
    between(year) |
    year |
    each

  private def yearsEvery(base: AttoParser[ConstNode]): AttoParser[EveryNode] = {
    def compose(b: => AttoParser[DivisibleNode]) =
      ((b <~ slash) ~ int.filter(_ > 0)).map {
        case (exp, freq) => EveryNode(exp, freq)
      }
    compose(several(base) | between(base) | each)
  }

  private val cron: AttoParser[CronExpr] = for {
    sec     <- field(seconds) <~ blank
    min     <- field(minutes) <~ blank
    hour    <- field(hours) <~ blank
    day     <- fieldWithAny(daysOfMonth) <~ blank
    month   <- field(months) <~ blank
    weekDay <- fieldWithAny(daysOfWeek)
    year    <- opt(blank ~> fieldForYear)
  } yield CronExpr(sec, min, hour, day, month, weekDay, year)

  def parse(e: String): Either[Error, CronExpr] =
    (phrase(cron).parseOnly(e): @unchecked) match {
      case ParseResult.Done(_, result)    => Right(result)
      case ParseResult.Fail("", _, _)     => Left(ExprTooShort)
      case ParseResult.Fail(rest, _, msg) =>
        val position = e.length() - rest.length() + 1
        Left(ParseFailed(msg, position, Some(rest)))
    }
}
