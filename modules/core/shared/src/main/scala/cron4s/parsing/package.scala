package cron4s

import cats.syntax.either._

package object parsing {

  private[cron4s] def parse(e: String): Either[Error, CronExpr] =
    for {
      tokens <- CronLexer.tokenize(e)
      expr   <- CronParser.read(tokens)
    } yield expr

}
