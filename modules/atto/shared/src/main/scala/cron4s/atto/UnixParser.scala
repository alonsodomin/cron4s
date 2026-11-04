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

trait UnixParser extends cron4s.parser.Parser with CronExprParser {

  import cron4s.parser._
  import cron4s.parser.Node._

  // ----------------------------------------
  // AST Parsing & Building
  // ----------------------------------------

  protected def field(base: AttoParser[ConstNode]): AttoParser[NodeWithoutAny] =
    every(base) |
      several(base) |
      between(base) |
      base |
      each

  protected def fieldWithAny(base: AttoParser[ConstNode]): AttoParser[Node] =
    every(base) |
      several(base) |
      between(base) |
      base |
      each |
      any

  protected val unixCronNodes
      : AttoParser[(NodeWithoutAny, NodeWithoutAny, NodeWithoutAny, Node, NodeWithoutAny, Node)] =
    for {
      sec     <- field(seconds) <~ blank
      min     <- field(minutes) <~ blank
      hour    <- field(hours) <~ blank
      day     <- fieldWithAny(daysOfMonth) <~ blank
      month   <- field(months) <~ blank
      weekDay <- fieldWithAny(daysOfWeek)
    } yield (sec, min, hour, day, month, weekDay)

  private val cron: AttoParser[CronExpr] =
    unixCronNodes.map {
      case (sec, min, hour, day, month, weekDay) =>
        CronExpr(sec, min, hour, day, month, weekDay, None)
    }

  def parse(e: String): Either[Error, CronExpr] =
    (phrase(cron).parseOnly(e): @unchecked) match {
      case ParseResult.Done(_, result)    => Right(result)
      case ParseResult.Fail("", _, _)     => Left(ExprTooShort)
      case ParseResult.Fail(rest, _, msg) =>
        val position = e.length() - rest.length() + 1
        Left(ParseFailed(msg, position, Some(rest)))
    }
}
