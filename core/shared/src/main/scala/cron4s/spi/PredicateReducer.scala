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
        (field: F, expr: FieldExpr[F])
        (implicit ev: Lazy[IsFieldExpr[FieldExpr, F]]): Predicate[DateTime] =
      Predicate { dt =>
        adapter.get(dt, field).map(ev.value.matches(expr)).getOrElse(!M.empty[DateTime](dt))
      }

    implicit def caseSeconds     = at[SecondsExpr](expr => predicateFor(Second, expr))
    implicit def caseMinutes     = at[MinutesExpr](expr => predicateFor(Minute, expr))
    implicit def caseHours       = at[HoursExpr](expr => predicateFor(Hour, expr))
    implicit def caseDaysOfMonth = at[DaysOfMonthExpr](expr => predicateFor(DayOfMonth, expr))
    implicit def caseMonths      = at[MonthsExpr](expr => predicateFor(Month, expr))
    implicit def caseDaysOfWeek  = at[DaysOfWeekExpr](expr => predicateFor(DayOfWeek, expr))
  }

  def run(expr: CronExpr): Predicate[DateTime] =
    expr.repr.map(exprToMatcher).toList
      .foldLeft(M.empty[DateTime])((lhs, rhs) => M.plus(lhs, rhs))

}
