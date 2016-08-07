package cron4s.ext

import cron4s._
import cron4s.expr._
import cron4s.matcher.Matcher

import shapeless._

import scala.annotation.tailrec

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

  private[this] def stepDayOfWeek(dt: DateTime, expr: Expr[DayOfWeek.type], stepSize: Int): Step = {
    for {
      dayOfWeek         <- adapter.get(dt, DayOfWeek)
      (value, nextStep) <- expr.step(dayOfWeek, stepSize)
      newDateTime       <- adapter.set(dt, DayOfWeek, value)
      newDayOfWeek      <- adapter.get(dt, DayOfWeek)
    } yield newDateTime -> (nextStep + (newDayOfWeek - dayOfWeek))
  }

  object steppingTime extends Poly {
    implicit def caseMinutes     = use(stepAndAdjust[Minute.type] _)
    implicit def caseHours       = use(stepAndAdjust[Hour.type] _)
  }

  object steppingDate extends Poly {
    implicit def caseDaysOfMonth = use(stepAndAdjust[DayOfMonth.type] _)
    implicit def caseMonths      = use(stepAndAdjust[Month.type] _)
  }

  def run(expr: CronExpr): Option[DateTime] = {
    implicit val conjuction = Matcher.conjunction
    val matching = new MatcherReducer[DateTime].run(expr)

    def stepDatePart(previous: Step): Step = {
      val dateWithoutWeekOfDay = expr.datePart.take(2)
      dateWithoutWeekOfDay.foldLeft(previous)(steppingDate).flatMap {
        case (dt, stepSize) => stepDayOfWeek(dt, expr.daysOfWeek, stepSize)
      }
    }

    @tailrec
    def dateStepLoop(previous: Step): Step = previous match {
      case None => previous
      case Some((dateTime, nextStep)) if nextStep != 0 =>
        dateStepLoop(stepDatePart(previous))

      case Some((dateTime, _)) =>
        if (matching(dateTime)) previous
        else {
          val nextStep: Step = Some(dateTime -> 1)
          stepDatePart(nextStep)
        }
    }

    val initial: Step = Some((from, initialStep))
    val timeAdjusted: Step = expr.timePart.foldLeft(initial)(steppingTime)
    val adjusted = dateStepLoop(timeAdjusted)
    adjusted.map(_._1)
  }

}
