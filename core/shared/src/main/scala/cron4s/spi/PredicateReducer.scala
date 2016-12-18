package cron4s.spi

import cron4s._
import cron4s.expr._
import cron4s.types._

import shapeless._

import scalaz.PlusEmpty

/**
  * Created by domingueza on 29/07/2016.
  */
private[spi] final class PredicateReducer[DateTime]
    (implicit M: PlusEmpty[Predicate], adapter: DateTimeAdapter[DateTime]) {

  object exprToMatcher extends Poly1 {
    import CronField._

    private[this] def predicateFor[F <: CronField]
        (field: F, expr: FieldExprAST[F])
        (implicit ev: Lazy[IsFieldExpr[FieldExprAST, F]]): Predicate[DateTime] =
      Predicate { dt =>
        adapter.get(dt, field).map(ev.value.matches(expr)).getOrElse(!M.empty[DateTime](dt))
      }

    implicit def caseSeconds     = at[SecondsAST](expr => predicateFor(Second, expr))
    implicit def caseMinutes     = at[MinutesAST](expr => predicateFor(Minute, expr))
    implicit def caseHours       = at[HoursAST](expr => predicateFor(Hour, expr))
    implicit def caseDaysOfMonth = at[DaysOfMonthAST](expr => predicateFor(DayOfMonth, expr))
    implicit def caseMonths      = at[MonthsAST](expr => predicateFor(Month, expr))
    implicit def caseDaysOfWeek  = at[DaysOfWeekAST](expr => predicateFor(DayOfWeek, expr))
  }

  def run(expr: CronExpr): Predicate[DateTime] =
    expr.ast.map(exprToMatcher).toList
      .foldLeft(M.empty[DateTime])((lhs, rhs) => M.plus(lhs, rhs))

}
