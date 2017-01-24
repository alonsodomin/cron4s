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

/**
  * Entry point for the CRON parsing operation
  *
  * @author Antonio Alonso Dominguez
  */
object Cron {

  def apply(e: String): Either[InvalidCron, CronExpr] =
    parse(e).right.flatMap(validation.validateCron)

  private[this] def parse(e: String): Either[ParseFailed, CronExpr] = {
    parser.cron.parse(e) match {
      case Parsed.Success(expr, _) =>
        Right(expr)

      case err @ Parsed.Failure(_, idx, _) =>
        val error = ParseError(err)
        Left(ParseFailed(error.failure.msg, idx))
    }
  }

}
