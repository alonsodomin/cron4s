package cron4s

import cron4s.expr._
import cron4s.parser.ExprParser
import org.parboiled2.ParseError

import scala.util.{Failure, Success}

/**
  * Created by alonsodomin on 02/01/2016.
  */
object Cron {

  /*private object parser extends ASTParsers
  import parser._

  def apply(expr: String): Either[String, CronExpr] = {
    parser.parseAll(parser.cron, expr) match {
      case Success(e, _)     => Right(e)
      case NoSuccess(msg, _) => Left(msg)
    }
  }*/

  def apply(expr: String): Either[String, CronExpr] = {
    val parser = new ExprParser(expr)
    parser.InputExpr.run() match {
      case Success(e)             => Right(e)
      case Failure(e: ParseError) => Left(parser.formatError(e))
      case Failure(other)         => Left(other.getClass.getName)
    }
  }

}
