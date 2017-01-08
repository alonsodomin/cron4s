package cron4s

import cron4s.expr._

import shapeless._

/**
  * Created by alonsodomin on 08/01/2017.
  */
package object spi {

  type AST = CronExprAST :+: TimePartAST :+: DatePartAST :+: CNil

  private[spi] object extract extends Poly1 {
    implicit def caseTimePart = at[TimePartAST](identity)
    implicit def caseDatePart = at[DatePartAST](identity)
    implicit def caseCronExpr = at[CronExprAST](identity)
  }

}
