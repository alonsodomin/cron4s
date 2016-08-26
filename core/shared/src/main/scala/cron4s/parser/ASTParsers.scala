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

  def expr[F <: CronField](p: Parser[ConstExpr[F]])(implicit unit: CronUnit[F]): Parser[Expr[F]] = {
    def everyAST(p: Parser[ConstExpr[F]]): Parser[Expr[F]] =
      every[Expr, F](severalAST(p) | between(p) | any)

    def severalAST(p: Parser[ConstExpr[F]]): Parser[Expr[F]] = {
      val betweenOrConst = between(p) | p
      several(betweenOrConst)
    }

    everyAST(p) | severalAST(p) | between(p) | p | any
  }

  val minutes     : Parser[MinutesExpr]     = expr[Minute.type](minute)
  val hours       : Parser[HoursExpr]       = expr[Hour.type](hour)
  val daysOfMonth : Parser[DaysOfMonthExpr] = expr[DayOfMonth.type](dayOfMonth)
  val months      : Parser[MonthsExpr]      = expr[Month.type](month)
  val daysOfWeek  : Parser[DaysOfWeekExpr]  = expr[DayOfWeek.type](dayOfWeek)

  def cron: Parser[CronExpr] = minutes ~ hours ~ daysOfMonth ~ months ~ daysOfWeek ^^ {
    case m ~ h ~ dm ~ mm ~ dw => CronExpr(m :: h :: dm :: mm :: dw :: HNil)
  }

}
