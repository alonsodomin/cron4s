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

import cron4s.parser
import cron4s.parser.CronExpr
import atto.ParseResult
import _root_.atto.{Parser => AttoParser, _}
import atto.Atto._
import cron4s.parser.Node._
import cron4s.parser._

object AWSEventBridgeScheduleParser extends cron4s.parser.Parser with CronExprParser {

  private[atto] val year: AttoParser[ConstNode] = NDigitsPositiveInt(4).flatMap {
    case n /*if 1970 <= n && n <= 2199 */=> ok(ConstNode(n, None))
    // case _                           =>
    //   err(
    //     "AWS year must be between 1970 and 2199: "
    //       + "https://docs.aws.amazon.com/eventbridge/latest/userguide/eb-scheduled-rule-pattern.html#eb-cron-expressions"
    //   )
  }
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

  private val cron: AttoParser[CronExpr] = for {
    min     <- field(minutes) <~ blank
    hour    <- field(hours) <~ blank
    day     <- fieldWithAny(daysOfMonth) <~ blank
    month   <- field(months) <~ blank
    weekDay <- fieldWithAny(daysOfWeek) <~ blank
    year    <- field(year)
  } yield CronExpr(Node.ConstNode(0), min, hour, day, month, weekDay, Some(year))

  def parse(e: String): Either[Error, CronExpr] =
    (phrase(cron).parseOnly(e): @unchecked) match {
      case ParseResult.Done(_, result)    => Right(result)
      case ParseResult.Fail("", _, _)     => Left(parser.ExprTooShort)
      case ParseResult.Fail(rest, _, msg) =>
        val position = e.length() - rest.length() + 1
        Left(parser.ParseFailed(msg, position, Some(rest)))
    }
}
