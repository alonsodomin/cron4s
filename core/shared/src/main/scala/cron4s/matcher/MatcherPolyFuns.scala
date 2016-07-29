package cron4s.matcher

import cats.MonoidK
import cron4s.CronField
import cron4s.expr._
import shapeless._

/**
  * Created by domingueza on 29/07/2016.
  */
class MatcherPolyFuns[A](implicit M: MonoidK[Matcher], extract: FieldExtractor[A]) {

  private[this] def buildMatcher[F <: CronField](field: F, expr: Expr[F]): Matcher[A] = Matcher { a =>
    extract(field, a).map(expr.matcher.matches).getOrElse(M.empty[A].matches(a))
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
}
