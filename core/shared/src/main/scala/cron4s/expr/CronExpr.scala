package cron4s.expr

import cron4s.{CronField, CronUnit}

import shapeless._

/**
  * Representation of a valid CRON expression as an AST
  *
  * @author Antonio Alonso Dominguez
  */
final case class CronExpr(
    seconds: SecondsExpr,
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
  lazy val timePart: TimePartExpr = TimePartExpr(seconds, minutes, hours)

  /**
    * Date part of the CRON expression
    */
  lazy val datePart: DatePartExpr = DatePartExpr(daysOfMonth, months, daysOfWeek)

  /**
    * Generic field accessor. Given a CronField, this method can be used
    * to access the expression on that given field.
    *
    * @param unit the CronUnit for the given field
    * @tparam F CronField type
    * @return field-based expression for given field
    */
  def field[F <: CronField](implicit unit: CronUnit[F]): FieldExpr[F] = unit.field match {
    case CronField.Second     => seconds.asInstanceOf[FieldExpr[F]]
    case CronField.Minute     => minutes.asInstanceOf[FieldExpr[F]]
    case CronField.Hour       => hours.asInstanceOf[FieldExpr[F]]
    case CronField.DayOfMonth => daysOfMonth.asInstanceOf[FieldExpr[F]]
    case CronField.Month      => months.asInstanceOf[FieldExpr[F]]
    case CronField.DayOfWeek  => daysOfWeek.asInstanceOf[FieldExpr[F]]
  }

  def ranges: List[Vector[Int]] = repr.map(cron4s.util.range).toList

  override def toString = repr.map(cron4s.util.show).toList.mkString(" ")

}
