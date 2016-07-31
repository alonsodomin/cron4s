package cron4s.ext

import cron4s._
import cron4s.expr._

import shapeless._

private[ext] final class Stepper[DateTime](from: DateTime, initialStep: Int)(implicit adapter: DateTimeAdapter[DateTime]) {
  import CronField._

  private[this] def stepField[F <: CronField](expr: Expr[F], step: Int) =
    adapter.get(from, expr.unit.field).flatMap(v => expr.step(v, step))

  object folding extends Poly {
    private[this] def stepAndAdjust[F <: CronField](expr: Expr[F], dateTimeAndStep: Option[(DateTime, Int)]): Option[(DateTime, Int)] = {
      for {
        (dateTime, step)  <- dateTimeAndStep
        (value, nextStep) <- stepField(expr, step)
        newDateTime       <- adapter.set(dateTime, expr.unit.field, value)
      } yield (newDateTime, nextStep)
    }

    implicit def caseMinutes     = use(stepAndAdjust[Minute.type] _)
    implicit def caseHours       = use(stepAndAdjust[Hour.type] _)
    implicit def caseDaysOfMonth = use(stepAndAdjust[DayOfMonth.type] _)
    implicit def caseMonths      = use(stepAndAdjust[Month.type] _)
  }

  def run(expr: CronExpr): Option[DateTime] = {
    val subExpr = expr.repr.take(4)
    val initial: Option[(DateTime, Int)] = Some((from, initialStep))
    val adjusted = subExpr.foldRight(initial)(folding)

    // TODO need to adjust weekdays
    adjusted.map(_._1)
  }

}
