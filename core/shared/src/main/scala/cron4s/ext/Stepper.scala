package cron4s.ext

import cron4s._
import cron4s.expr._
import cron4s.matcher.Matcher

import shapeless._

private[ext] final class Stepper[DateTime](from: DateTime, initialStep: Int)(implicit adapter: DateTimeAdapter[DateTime]) {
  import CronField._

  type Step = Option[(DateTime, Int)]

  private[this] def stepField[F <: CronField](expr: Expr[F], step: Int) =
    adapter.get(from, expr.unit.field).flatMap(v => expr.step(v, step))

  private[this] def stepAndAdjust[F <: CronField](dateTimeAndStep: Step, expr: Expr[F]): Step = {
    for {
      (dateTime, step)  <- dateTimeAndStep
      (value, nextStep) <- stepField(expr, step)
      newDateTime       <- adapter.set(dateTime, expr.unit.field, value)
    } yield (newDateTime, nextStep)
  }

  object steppingTime extends Poly {
    implicit def caseMinutes     = use(stepAndAdjust[Minute.type] _)
    implicit def caseHours       = use(stepAndAdjust[Hour.type] _)
  }

  object steppingDate extends Poly {
    implicit def caseDaysOfMonth = use(stepAndAdjust[DayOfMonth.type] _)
    implicit def caseMonths      = use(stepAndAdjust[Month.type] _)
    implicit def caseDayOfWeek   = use(stepAndAdjust[DayOfWeek.type] _)
  }

  def run(expr: CronExpr): Option[DateTime] = {
    implicit val conjuction = Matcher.conjunction
    val matching = new MatcherReducer[DateTime].run(expr)

    def dateStepLoop(previous: Step): Step = previous match {
      case None => previous
      case Some((dateTime, nextStep)) if nextStep != 0 =>
        dateStepLoop(expr.datePart.foldLeft(previous)(steppingDate))

      case Some((dateTime, _)) =>
        if (matching(dateTime)) previous
        else dateStepLoop(expr.datePart.foldLeft(previous)(steppingDate))
    }

    val initial: Step = Some((from, initialStep))
    val timeAdjusted: Step = expr.timePart.foldLeft(initial)(steppingTime)
    val adjusted = dateStepLoop(timeAdjusted)
    adjusted.map(_._1)
  }

}
