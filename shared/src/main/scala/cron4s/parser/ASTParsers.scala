package cron4s.parser

import cron4s.expr._

import shapeless._

/**
  * Created by alonsodomin on 02/01/2016.
  */

trait ASTParsers extends ExprParsers {
  import CronField._
  import Expr._

  private[this] def expr[F <: CronField : CronUnit](p: Parser[ConstExpr[F]]): Parser[Expr[F]] = {
    def everyAST(p: Parser[ConstExpr[F]]): Parser[EveryExpr[F]] =
      every(severalAST(p) | between(p) | always)

    def severalAST(p: Parser[ConstExpr[F]]): Parser[SeveralExpr[F]] =
      several(between(p) | p)

    everyAST(p) | severalAST(p) | between(p) | p | always
  }

  val minutes = expr[Minute.type](minute)
  val hours = expr[Hour.type](hour)
  val dayOfMonths = expr[DayOfMonth.type](dayOfMonth)
  val months = expr[Month.type](month)
  val daysOfWeek = expr[DayOfWeek.type](dayOfWeek)

  def cron: Parser[CronExpr] = minutes ~ hours ~ dayOfMonths ~ months ~ daysOfWeek ^^ {
    case m ~ h ~ dm ~ mm ~ dw => CronExpr(m :: h :: dm :: mm :: dw :: HNil)
  }

}
