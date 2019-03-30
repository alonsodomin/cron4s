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

sealed trait CronToken extends Positional
object CronToken {
  case class Number(value: Int) extends CronToken {
    override def toString = value.toString
  }
  case class Text(value: String) extends CronToken {
    override def toString = value.toString
  }
  case object Hyphen extends CronToken {
    override def toString = "-"
  }
  case object Slash extends CronToken {
    override def toString = "/"
  }
  case object Comma extends CronToken {
    override def toString = ","
  }
  case object Asterisk extends CronToken {
    override def toString = "*"
  }
  case object QuestionMark extends CronToken {
    override def toString = "?"
  }
  case object Blank extends CronToken {
    override def toString = " "
  }

  case object Last extends CronToken {
    override def toString = "L"
  }

  case object WeekDay extends CronToken {
    override def toString = "W"
  }

  case object Hash extends CronToken {
    override def toString = "#"
  }
}

object CronLexer extends RegexParsers with BaseParser {
  import CronToken._

  override def skipWhitespace = false
  override val whiteSpace     = "[ \t\r\f]+".r

  private val number = positioned {
    """\d+""".r ^^ { x =>
      Number(x.toInt)
    }
  }

  private val text = positioned {
    """[a-zA-Z]+""".r ^^ { Text(_) }
  }

  private val asterisk = positioned {
    """\*""".r ^^ { _ =>
      Asterisk
    }
  }

  private val questionMark = positioned {
    """\?""".r ^^ { _ =>
      QuestionMark
    }
  }

  private val hyphen = positioned {
    """\-""".r ^^ { _ =>
      Hyphen
    }
  }

  private val slash = positioned {
    """\/""".r ^^ { _ =>
      Slash
    }
  }

  private val comma = positioned {
    ",".r ^^ { _ =>
      Comma
    }
  }

  private val blank = positioned {
    whiteSpace.map(_ => Blank)
  }

  private val last = positioned {
    "L".r ^^ { _ =>
      Last
    }
  }

  private val weekDay = positioned {
    "W".r ^^ { _ =>
      WeekDay
    }
  }

  private val hash = positioned {
    "#".r ^^ { _ =>
      Hash
    }
  }

  private lazy val tokens: Parser[List[CronToken]] = {
    phrase(
      rep1(
        number | text | hyphen | slash | comma | asterisk | questionMark | last | weekDay | hash | blank
      )
    )
  }

  def tokenize(expr: String): Either[ParseFailed, List[CronToken]] =
    parse(tokens, expr) match {
      case err: NoSuccess     => Left(handleError(err))
      case Success(result, _) => Right(result)
    }

}
