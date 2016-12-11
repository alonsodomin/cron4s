package cron4s.expr

import cron4s.{CronField, CronUnit}

import shapeless._

/**
  * Representation of a valid CRON expression as an AST
  *
  * @author Antonio Alonso Dominguez
  */
final case class CronExpr(
    seconds: SecondExpr,
    minutes: MinutesExpr,
    hours: HoursExpr,
    daysOfMonth: DaysOfMonthExpr,
    months: MonthsExpr,
    daysOfWeek: DaysOfWeekExpr
  ) {

  private[cron4s] lazy val repr: CronExprRepr = Generic[CronExpr].to(this)

  /**
    * Time part of the CRON expression
    */
  lazy val timePart: TimePartExpr = new TimePartExpr(repr.take(3))
  /**
    * Date part of the CRON expression
    */
  lazy val datePart: DatePartExpr = new DatePartExpr(repr.drop(3))

  /**
    * Generic field accessor. Given a CronField, this method can be used
    * to access the expression on that given field.
    *
    * @param f CronField
    * @tparam F CronField type
    * @return field-based expression for given field
    */
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
