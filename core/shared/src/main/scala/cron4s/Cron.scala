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

  private[this] def parse(e: String): Either[ParseFailed, CronExprAST] = {
    parser.cron.parse(e) match {
      case Parsed.Success(expr, _) =>
        Right(expr)

      case err @ Parsed.Failure(_, idx, _) =>
        val error = ParseError(err)
        Left(ParseFailed(error.failure.msg, idx))
    }
  }

}
