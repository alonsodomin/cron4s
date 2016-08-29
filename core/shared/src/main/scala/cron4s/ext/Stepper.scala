package cron4s.ext

import cron4s._
import cron4s.expr._
import cron4s.types._

import shapeless._

import scala.annotation.tailrec

private[ext] final class Stepper[DateTime](from: DateTime, initialStep: Int)(implicit adapter: DateTimeAdapter[DateTime]) {
  import CronField._

  type Step = Option[(DateTime, Int)]

  private[this] def stepField[F <: CronField]
      (expr: Expr[F], step: Int)
      (implicit ev: IsFieldExpr[Expr, F]): Option[(Int, Int)] =
    adapter.get(from, expr.unit.field).flatMap(v => ev.step(expr)(v, step))

  private[this] def stepAndAdjust[F <: CronField]
      (dateTimeAndStep: Step, expr: Expr[F]): Step = {
    for {
      (dateTime, step)  <- dateTimeAndStep
      (value, nextStep) <- stepField(expr, step)
      newDateTime       <- adapter.set(dateTime, expr.unit.field, value)
    } yield (newDateTime, nextStep)
  }

  private[this] def stepDayOfWeek
      (dt: DateTime, expr: Expr[DayOfWeek.type], stepSize: Int)
      (implicit ev: IsFieldExpr[Expr, DayOfWeek.type]): Step = {
    for {
      dayOfWeek         <- adapter.get(dt, DayOfWeek)
      (value, nextStep) <- ev.step(expr)(dayOfWeek, stepSize)
      newDateTime       <- adapter.set(dt, DayOfWeek, value)
      newDayOfWeek      <- adapter.get(dt, DayOfWeek)
    } yield newDateTime -> (nextStep + (newDayOfWeek - dayOfWeek))
  }

  object steppingTime extends Poly {
    implicit def caseSeconds     = use((step: Step, expr: SecondExpr) => stepAndAdjust(step, expr))
    implicit def caseMinutes     = use((step: Step, expr: MinutesExpr) => stepAndAdjust(step, expr))
    implicit def caseHours       = use((step: Step, expr: HoursExpr) => stepAndAdjust(step, expr))
  }

  object steppingDate extends Poly {
    implicit def caseDaysOfMonth = use((step: Step, expr: DaysOfMonthExpr) => stepAndAdjust(step, expr))
    implicit def caseMonths      = use((step: Step, expr: MonthsExpr) => stepAndAdjust(step, expr))
  }

  def run(expr: CronExpr): Option[DateTime] = {
    val matches = {
      implicit val conjuction = Predicate.conjunction
      new PredicateReducer[DateTime].run(expr)
    }

    val dateWithoutWeekOfDay = expr.datePart.take(2)

    def stepDatePart(previous: Step): Step =
      dateWithoutWeekOfDay.foldLeft(previous)(steppingDate).flatMap {
        case (dt, stepSize) => stepDayOfWeek(dt, expr.daysOfWeek, stepSize)
      }

    @tailrec
    def dateStepLoop(previous: Step): Step = {
      val dateAdjusted = stepDatePart(previous)
      dateAdjusted match {
        case Some((dateTime, nextStep)) if nextStep != 0 =>
          dateStepLoop(stepDatePart(dateAdjusted))

        case Some((dateTime, _)) if !matches(dateTime) =>
          val nextStep: Step = Some(dateTime -> 1)
          stepDatePart(nextStep)

        case _ => dateAdjusted
      }
    }

    val initial: Step = Some(from -> initialStep)
    val timeAdjusted: Step = expr.timePart.foldLeft(initial)(steppingTime)
    val adjusted = dateStepLoop(timeAdjusted)
    adjusted.map(_._1)
  }

}
