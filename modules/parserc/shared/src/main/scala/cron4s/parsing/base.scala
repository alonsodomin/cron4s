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
package parsing

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{Position, NoPosition}

private[parsing] trait BaseParser extends Parsers {
  protected def handleError(err: NoSuccess): _root_.cron4s.Error =
    err.next.pos match {
      case NoPosition => ExprTooShort
      case pos: Position =>
        val position = err.next.pos.column
        val found = {
          if (err.next.atEnd) None
          else Option(err.next.first.toString).filter(_.nonEmpty)
        }
        ParseFailed(err.msg, position, found)
    }
}
