package cron4s.expr

import cron4s.{CronField, CronUnit}

import shapeless._

/**
  * Created by domingueza on 29/07/2016.
  */
final case class CronExpr(
    seconds: SecondExpr,
    minutes: MinutesExpr,
    hours: HoursExpr,
    daysOfMonth: DaysOfMonthExpr,
    months: MonthsExpr,
    daysOfWeek: DaysOfWeekExpr
  ) {

  lazy val repr: CronExprRepr = Generic[CronExpr].to(this)
  lazy val timePart: TimePartExpr = new TimePartExpr(repr.take(3))
  lazy val datePart: DatePartExpr = new DatePartExpr(repr.drop(3))

  def field[F <: CronField : CronUnit](f: F): Expr[F] = f match {
    case CronField.Second     => seconds.asInstanceOf[Expr[F]]
    case CronField.Minute     => minutes.asInstanceOf[Expr[F]]
    case CronField.Hour       => hours.asInstanceOf[Expr[F]]
    case CronField.DayOfMonth => daysOfMonth.asInstanceOf[Expr[F]]
    case CronField.Month      => months.asInstanceOf[Expr[F]]
    case CronField.DayOfWeek  => daysOfWeek.asInstanceOf[Expr[F]]
  }

  override def toString = s"$seconds $minutes $hours $daysOfMonth $months $daysOfWeek"

}
