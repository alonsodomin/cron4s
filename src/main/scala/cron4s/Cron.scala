package cron4s

import cron4s.expr.CronExpr
import cron4s.parser.ASTParsers

/**
  * Created by alonsodomin on 02/01/2016.
  */
object Cron {

  object parser extends ASTParsers

  def apply(expr: String) = parser.parseAll(parser.cron, expr)

}
