package cron4s.parser

import cron4s.{CronField, CronUnit}
import cron4s.expr._
import cron4s.types._
import shapeless._

/**
  * Created by alonsodomin on 02/01/2016.
  */

trait ASTParsers extends ExprParsers {
  import CronField._

  override val whiteSpace = """\s+""".r

  override val skipWhitespace = false

  def expr[F <: CronField](p: Parser[ConstExpr[F]])(implicit unit: CronUnit[F]): Parser[Expr[F]] = {
    def everyAST(p: Parser[ConstExpr[F]]): Parser[EveryExpr[F]] =
      every(severalAST(p) | between(p) | any)

    def severalAST(p: Parser[ConstExpr[F]]): Parser[SeveralExpr[F]] =
      several(between(p) | p)

    (everyAST(p) | severalAST(p) | between(p) | p | any) <~ whiteSpace.?
  }

  val seconds     : Parser[SecondExpr]      = expr[Second.type](second)
  val minutes     : Parser[MinutesExpr]     = expr[Minute.type](minute)
  val hours       : Parser[HoursExpr]       = expr[Hour.type](hour)
  val daysOfMonth : Parser[DaysOfMonthExpr] = expr[DayOfMonth.type](dayOfMonth)
  val months      : Parser[MonthsExpr]      = expr[Month.type](month)
  val daysOfWeek  : Parser[DaysOfWeekExpr]  = expr[DayOfWeek.type](dayOfWeek)

  def cron: Parser[CronExpr] = seconds ~ minutes ~ hours ~ daysOfMonth ~ months ~ daysOfWeek ^^ {
    case s ~ m ~ h ~ dm ~ mm ~ dw => CronExpr(s, m, h, dm, mm, dw)
  }

}
