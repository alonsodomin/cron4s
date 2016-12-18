package cron4s.spi

import cron4s._
import cron4s.expr._
import cron4s.types._

import shapeless._

import scala.annotation.tailrec

private[spi] final class Stepper[DateTime](from: DateTime, initialStep: Int)(implicit adapter: DateTimeAdapter[DateTime]) {
  import CronField._

  type Step = Option[(DateTime, Int)]

  private[this] def stepField[F <: CronField]
      (expr: FieldExpr[F], step: Int): Option[(Int, Int)] =
    adapter.get(from, expr.unit.field)
      .flatMap(v => expr.step(v, step))

  private[this] def stepAndAdjust[F <: CronField]
      (dateTimeAndStep: Step, expr: FieldExpr[F]): Step = {
    for {
      (dateTime, step)  <- dateTimeAndStep
      (value, nextStep) <- stepField(expr, step)
      newDateTime       <- adapter.set(dateTime, expr.unit.field, value)
    } yield (newDateTime, nextStep)
  }

  private[this] def stepDayOfWeek
      (dt: DateTime, expr: FieldExpr[DayOfWeek], stepSize: Int): Step = {
    for {
      dayOfWeek         <- adapter.get(dt, DayOfWeek)
      (value, nextStep) <- expr.step(dayOfWeek, stepSize)
      newDateTime       <- adapter.set(dt, DayOfWeek, value)
      newDayOfWeek      <- adapter.get(dt, DayOfWeek)
    } yield newDateTime -> (nextStep + (newDayOfWeek - dayOfWeek))
  }

  object stepping extends Poly2 {
    implicit def caseSeconds     = at[Step, SecondsExpr](stepAndAdjust)
    implicit def caseMinutes     = at[Step, MinutesExpr](stepAndAdjust)
    implicit def caseHours       = at[Step, HoursExpr](stepAndAdjust)
    implicit def caseDaysOfMonth = at[Step, DaysOfMonthExpr](stepAndAdjust)
    implicit def caseMonths      = at[Step, MonthsExpr](stepAndAdjust)
  }

  def run(expr: CronExpr): Option[DateTime] = {
    val matches = {
      implicit val conjuction = Predicate.conjunction
      new PredicateReducer[DateTime].run(expr)
    }

    val dateWithoutWeekOfDay = expr.datePart.repr.take(2)

    def stepDatePart(previous: Step): Step =
      dateWithoutWeekOfDay.foldLeft(previous)(stepping).flatMap {
        case (dt, stepSize) => stepDayOfWeek(dt, expr.daysOfWeek, stepSize)
      }

    @tailrec
    def dateStepLoop(previous: Step): Step = {
      val dateAdjusted = stepDatePart(previous)
      dateAdjusted match {
        case Some((_, nextStep)) if nextStep != 0 =>
          dateStepLoop(stepDatePart(dateAdjusted))

        case Some((dateTime, _)) if !matches(dateTime) =>
          val nextStep: Step = Some(dateTime -> 1)
          stepDatePart(nextStep)

        case _ => dateAdjusted
      }
    }

    val initial: Step = Some(from -> initialStep)
    val timeAdjusted: Step = expr.timePart.repr.foldLeft(initial)(stepping)
    val adjusted = dateStepLoop(timeAdjusted)
    adjusted.map(_._1)
  }

}
