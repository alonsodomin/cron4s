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

import _root_.doobie.{Meta, Read, Write}
import _root_.doobie.util.invariant.SecondaryValidationFailed

package object doobie {
  implicit val cronExprMeta: Meta[CronExpr] =
    Meta[String].imap(parseOrException)(_.toString)

  implicit val cronExprWrite: Write[CronExpr] =
    Write[String].contramap(_.toString)

  implicit val cronExprRead: Read[CronExpr] =
    Read[String].map(parseOrException)

  private def parseOrException(str: String): CronExpr =
    Cron.parse(str) match {
      case Right(expr) => expr
      case Left(err)   => throw new SecondaryValidationFailed[CronExpr](err.getMessage)
    }
}
