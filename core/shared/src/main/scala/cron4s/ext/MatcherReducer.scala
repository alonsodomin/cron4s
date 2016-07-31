package cron4s.ext

import cron4s._
import cron4s.expr._
import cron4s.types.MonoidK
import cron4s.matcher.Matcher

import shapeless._

/**
  * Created by domingueza on 29/07/2016.
  */
private[ext] final class MatcherReducer[A](implicit M: MonoidK[Matcher], adapter: DateTimeAdapter[A]) {

  private[this] def buildMatcher[F <: CronField](field: F, expr: Expr[F]): Matcher[A] = Matcher { a =>
    adapter.get(a, field).map(expr.matches(_)).getOrElse(!M.empty[A](a))
  }

  object exprToMatcher extends Poly1 {
    import CronField._

    implicit def caseMinutes     = at[MinutesExpr](expr => buildMatcher(Minute, expr))
    implicit def caseHours       = at[HoursExpr](expr => buildMatcher(Hour, expr))
    implicit def caseDaysOfMonth = at[DaysOfMonthExpr](expr => buildMatcher(DayOfMonth, expr))
    implicit def caseMonths      = at[MonthsExpr](expr => buildMatcher(Month, expr))
    implicit def caseDaysOfWeek  = at[DaysOfWeekExpr](expr => buildMatcher(DayOfWeek, expr))
  }

  object combine extends Poly {
    implicit def caseAny = use(M.combineK[A] _)
  }

  def run(expr: CronExpr) = expr.repr.map(exprToMatcher).foldLeft(M.empty[A])(combine)

}
