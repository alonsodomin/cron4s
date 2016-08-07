package cron4s.ext

import cron4s._
import cron4s.expr._
import cron4s.matcher.Matcher

import shapeless._

import scalaz.PlusEmpty

/**
  * Created by domingueza on 29/07/2016.
  */
private[ext] final class MatcherReducer[DateTime](implicit M: PlusEmpty[Matcher], adapter: DateTimeAdapter[DateTime]) {

  object exprToMatcher extends Poly1 {
    import CronField._

    private[this] def buildMatcher[F <: CronField](field: F, expr: Expr[F]): Matcher[DateTime] = Matcher { dt =>
      adapter.get(dt, field).map(expr.matches(_)).getOrElse(!M.empty[DateTime](dt))
    }

    implicit def caseMinutes     = at[MinutesExpr](expr => buildMatcher(Minute, expr))
    implicit def caseHours       = at[HoursExpr](expr => buildMatcher(Hour, expr))
    implicit def caseDaysOfMonth = at[DaysOfMonthExpr](expr => buildMatcher(DayOfMonth, expr))
    implicit def caseMonths      = at[MonthsExpr](expr => buildMatcher(Month, expr))
    implicit def caseDaysOfWeek  = at[DaysOfWeekExpr](expr => buildMatcher(DayOfWeek, expr))
  }

  object combine extends Poly {
    private[this] def fuseMatchers(left: Matcher[DateTime], right: Matcher[DateTime]): Matcher[DateTime] =
      M.plus(left, right)

    implicit def caseAny = use(fuseMatchers _)
  }

  def run(expr: CronExpr): Matcher[DateTime] = expr.repr.
    map(exprToMatcher).
    foldLeft(M.empty[DateTime])(combine)

}
