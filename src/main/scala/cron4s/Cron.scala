package cron4s

import cats.data.Xor
import cron4s.expr._
import cron4s.parser.ASTParsers

/**
  * Created by alonsodomin on 02/01/2016.
  */
object Cron {

  private object parser extends ASTParsers
  import parser._

  def apply(expr: String): String Xor CronExpr = {
    parser.parseAll(parser.cron, expr) match {
      case Success(e, _) => Xor.Right(e)
      case NoSuccess(msg, _) => Xor.Left(msg)
    }
  }

}
