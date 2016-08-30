package cron4s

import cron4s.expr._
import cron4s.parser._

import scala.util.parsing.input.CharArrayReader

/**
  * Created by alonsodomin on 02/01/2016.
  */
object Cron {

  private object parser extends ASTParsers
  import parser._

  def apply(expr: String): Either[String, CronExpr] = {
    val input = new CharArrayReader(expr.toCharArray)
    parser.parseAll(parser.cron, input) match {
      case Success(e, _)     => Right(e)
      case NoSuccess(msg, _) => Left(msg)
    }
  }

}
