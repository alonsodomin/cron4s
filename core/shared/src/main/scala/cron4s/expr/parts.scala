package cron4s.expr

import shapeless._

final case class DatePartExpr(
    daysOfMonth: DaysOfMonthExpr,
    months: MonthsExpr,
    daysOfWeek: DaysOfWeekExpr
  ) {

  private[cron4s] lazy val repr: DatePartRepr = Generic[DatePartExpr].to(this)

  override def toString = repr.map(cron4s.util.show).toList.mkString(" ")

}

final case class TimePartExpr(
    seconds: SecondsExpr,
    minutes: MinutesExpr,
    hours: HoursExpr
  ) {

  private[cron4s] lazy val repr: TimePartRepr = Generic[TimePartExpr].to(this)

  override def toString = repr.map(cron4s.util.show).toList.mkString(" ")

}