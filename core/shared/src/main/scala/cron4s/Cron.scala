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

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.{Failure, Success, Try}

/**
  * Created by domingueza on 10/04/2017.
  */
@JSExportTopLevel("cron4s.Cron")
object Cron {

  // Alias for parse
  def apply(e: String): Either[Error, CronExpr] = parse(e)

  def parse(e: String): Either[Error, CronExpr] =
    parse0(e).right.flatMap(validation.validateCron)

  def tryParse(e: String): Try[CronExpr] = parse(e) match {
    case Left(err)   => Failure(err)
    case Right(expr) => Success(expr)
  }

  def unsafeParse(e: String): CronExpr = parse(e) match {
    case Left(err)   => throw err
    case Right(expr) => expr
  }

  private[this] def parse0(e: String): Either[ParseFailed, CronExpr] = {
    parser.cron.parse(e) match {
      case Parsed.Success(expr, _) =>
        Right(expr)

      case Parsed.Failure(_, idx, extra) =>
        val input = extra.input
        val found = input.repr.literalize(input.slice(idx, idx + 20))
        Left(ParseFailed(extra.traced.expected, found, idx))
    }
  }

}
