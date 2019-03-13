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

import scala.util.parsing.input._
import scala.util.parsing.combinator._

sealed trait Token extends Positional
object Token {
  case class Sexagesimal(value: Int) extends Token
  case class Decimal(value: Int)     extends Token
  case class Literal(value: String)  extends Token
  case class Hyphen()                extends Token
  case class Slash()                 extends Token
  case class Comma()                 extends Token
  case class Star()                  extends Token
  case class Question()              extends Token
  case class Separator()             extends Token
}

object Lexer extends RegexParsers {
  import Token._

  override def skipWhitespace = false
  override val whiteSpace     = "[ \t\r\f]+".r

  private val sexagesimal = positioned {
    """[1-5][0-9]|0[0-9]|[0-9]""".r ^^ { x =>
      Sexagesimal(x.toInt)
    }
  }

  private val decimal = positioned {
    """\d+""".r ^^ { x =>
      Decimal(x.toInt)
    }
  }

  private val literal = positioned {
    """[a-zA-Z]+""".r ^^ { Literal(_) }
  }

  private val star = positioned {
    """\*""".r ^^ { _ =>
      Star()
    }
  }

  private val question = positioned {
    """\?""".r ^^ { _ =>
      Question()
    }
  }

  private val hyphen = positioned {
    """\-""".r ^^ { _ =>
      Hyphen()
    }
  }

  private val slash = positioned {
    """\/""".r ^^ { _ =>
      Slash()
    }
  }

  private val comma = positioned {
    ",".r ^^ { _ =>
      Comma()
    }
  }

  private val separator = positioned {
    whiteSpace.map(_ => Separator())
  }

  private lazy val tokens: Parser[List[Token]] = {
    phrase(
      rep1(
        sexagesimal | decimal | literal | hyphen | slash | comma | star | question | separator
      )
    )
  }

  def apply(expr: String): Either[LexerError, List[Token]] =
    parse(tokens, expr) match {
      case NoSuccess(msg, next) => Left(LexerError(msg, next.pos.column))
      case Success(result, _)   => Right(result)
    }

}
