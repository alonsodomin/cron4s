package cron4s

import cron4s.expr._
import cron4s.parser._
import cron4s.parser2.impl

import fastparse.all._

import scala.util.parsing.input.CharArrayReader

import scalaz._
import Scalaz._

/**
  * Entry point for the CRON parsing operation
  *
  * @author Antonio Alonso Dominguez
  */
object Cron {

  private object parser extends ASTParsers
  import parser._

  def apply(expr: String): Either[ParseFailed, CronExpr] = {
    val input = new CharArrayReader(expr.trim.toCharArray)
    parser.parseAll(parser.cron, input) match {
      case Success(e, _)        => Right(e)
      case NoSuccess(msg, next) => Left(ParseFailed(msg, expr.trim, next.pos))
    }
  }

  def apply2(e: String): Either[ExpressionError, CronExpr] = {
    \/.fromEither(parse(e)).flatMap(validation.validate).toEither
  }

  private[this] def parse(e: String): Either[ParseFailed, CronExpr] = {
    impl.cron.parse(e) match {
      case Parsed.Success(expr, _) =>
        Right(expr)

      case err @ Parsed.Failure(_, idx, _) =>
        val error = ParseError(err)
        Left(ParseFailed(error.getMessage, e, null))
    }
  }

}
