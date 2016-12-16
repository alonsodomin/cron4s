package cron4s

import cron4s.expr._

import fastparse.all._

import scalaz._

/**
  * Entry point for the CRON parsing operation
  *
  * @author Antonio Alonso Dominguez
  */
object Cron {

  def apply(e: String): Either[ParseFailed, CronExpr] = {
    \/.fromEither(parse(e))
      //.flatMap(validation.validate)
      .toEither
  }

  private[this] def parse(e: String): Either[InvalidCron, CronExpr] = {
    parser.cron.parse(e) match {
      case Parsed.Success(expr, _) =>
        Right(expr)

      case err @ Parsed.Failure(_, idx, _) =>
        val error = ParseError(err)
        Left(InvalidCron(error.failure.msg))
    }
  }

}
